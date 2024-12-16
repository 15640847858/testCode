import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.sun.management.OperatingSystemMXBean;

public class SystemMonitor {
    public static void main(String[] args) throws InterruptedException {
        // 获取 MemoryMXBean 来监控堆内存使用情况
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        while (true) {
            // 获取堆内存使用情况
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            long usedMemory = heapMemoryUsage.getUsed();
            long maxMemory = heapMemoryUsage.getMax();

            // 获取 CPU 占用率
            double cpuLoad = osBean.getProcessCpuLoad() * 100;

            // 输出监控信息
            System.out.printf("Heap Memory: Used = %d MB, Max = %d MB%n", usedMemory / 1024 / 1024, maxMemory / 1024 / 1024);
            System.out.printf("CPU Load: %.2f%%%n", cpuLoad);

            // 每秒输出一次
            Thread.sleep(1000);
        }
    }
}
