package com.ssssssnake.jvm.gc.status;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ssssssnake
 **/
public class GCMonitor {
    static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
    public static Set<String> youngGCNames = new HashSet<String>();
    public static Set<String> oldGCNames = new HashSet<String>();

    static {
        // Oracle HotSpot
        youngGCNames.add("Copy"); // -XX:+UseSerialGC
        youngGCNames.add("ParNew"); // -XX:+UseParNewGC
        youngGCNames.add("PS Scavenge"); // -XX:+UseParallelGC
        youngGCNames.add("G1 Young Generation"); // -XX:+UseG1GC

        // Oracle HotSpot
        oldGCNames.add("MarkSweepCompact"); // -XX:+UseSerialGC
        oldGCNames.add("PS MarkSweep"); // -XX:+UseParallelGC and
        // (-XX:+UseParallelOldGC or -XX:+UseParallelOldGCCompacting)
        oldGCNames.add("ConcurrentMarkSweep"); // -XX:+UseConcMarkSweepGC
        oldGCNames.add("G1 Old Generation"); // -XX:+UseG1GC

    }

    public static void main(String[] args) throws InterruptedException {
//        if (args == null || args.length == 0) {
////            System.err.println("Please specify the target PID to attach.");
////            return;
////        }

        // attach to the target application
        VirtualMachine vm;
        try {
            vm = VirtualMachine.attach("16604");
        } catch (AttachNotSupportedException e) {
            System.err.println("Target application doesn't support attach API.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("Error during attaching to target application.");
            e.printStackTrace();
            return;
        }

        try {
            // get the connector address
            String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            MBeanServerConnection serverConn;
            // no connector address, so we start the JMX agent
            if (connectorAddress == null) {
                String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib"
                        + File.separator + "management-agent.jar";
                vm.loadAgent(agent);
                // agent is started, get the connector address
                connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            }

            // establish connection to connector server
            JMXServiceURL url = new JMXServiceURL(connectorAddress);
            JMXConnector connector = JMXConnectorFactory.connect(url);
            serverConn = connector.getMBeanServerConnection();
            ObjectName objName = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);

            // Get standard attribute "VmVendor"
            String vendor = (String) serverConn.getAttribute(objName, "VmVendor");
            System.out.println("vendor:" + vendor);

            while (true) {
                long minorGCCount = 0;
                long minorGCTime = 0;
                long fullGCCount = 0;
                long fullGCTime = 0;

                for (String gcName : youngGCNames) {
                    objName = new ObjectName("java.lang:type=GarbageCollector,name=" + gcName);
                    try {
                        Long collectionCount = (Long) serverConn.getAttribute(objName, "CollectionCount");
                        Long collectionTime = (Long) serverConn.getAttribute(objName, "CollectionTime");
                        minorGCCount = collectionCount;
                        minorGCTime = collectionTime;
                    } catch (InstanceNotFoundException e) {

                    }
                }

                for (String gcName : oldGCNames) {
                    objName = new ObjectName("java.lang:type=GarbageCollector,name=" + gcName);
                    try {
                        Long collectionCount = (Long) serverConn.getAttribute(objName, "CollectionCount");
                        Long collectionTime = (Long) serverConn.getAttribute(objName, "CollectionTime");
                        fullGCCount = collectionCount;
                        fullGCTime = collectionTime;
                    } catch (InstanceNotFoundException e) {

                    }
                }


                StringBuilder valueStr = new StringBuilder();
                //custom data format is:
                //minorGCCount,minorGCTime,fullGCCount,fullGCTime
                valueStr.append(minorGCCount);
                valueStr.append(",");
                valueStr.append(minorGCTime);
                valueStr.append(",");
                valueStr.append(fullGCCount);
                valueStr.append(",");
                valueStr.append(fullGCTime);
                writeToFile(valueStr.toString());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getGCNames() {
        List<GarbageCollectorMXBean> gcmbeans = ManagementFactory.getGarbageCollectorMXBeans();
        String[] rtnName = new String[gcmbeans.size()];
        int index = 0;
        for (GarbageCollectorMXBean gc : gcmbeans) {
            rtnName[index] = gc.getName();
            index++;
        }
        return rtnName;
    }

    public static void writeToFile(String gcData) {
        String currDir = System.getProperty("user.dir");
        BufferedWriter writer = null;

        try {
            File customFile = new File(currDir + File.separator + "custom.data");
            if (!customFile.exists()) {
                customFile.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(customFile));
            writer.write(gcData);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error to read custom monitor data:" + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
