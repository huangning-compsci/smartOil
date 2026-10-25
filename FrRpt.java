import java.util.Scanner;

import devices.Equipment;
import exceptions.DeviceNotFoundException;
import exceptions.InvalidDeviceIdException;


public class FrRpt {

    public static void showResults(Scanner sc, Equipment[] results) {
    if (results.length == 0) System.out.println("没有符合条件的设备");
    else {
        System.out.println("共找到 " + results.length + " 台设备：");
        System.out.println(FrDev.showInfo(results));
    }
    FrUi.waitForEnter(sc);
    }


    public  static void queryDevices(Scanner sc) {
        System.out.println("1.按编号查询\n2.按类型查询\n3.按状态查询\n4.模糊查询（id/型号/井场）");
        String choice = sc.nextLine().trim();
        try {
            Equipment[] result;
            switch (choice) {
                case "1":
                    System.out.println("请输入设备id,例如 PU_1:");
                    result = new Equipment[]{FrDev.findById(sc.nextLine().trim())};
                    break;
                case "2":
                    System.out.println("请输入设备类型（PU/CP/PS/TS/FM/CM、英文类型名或中文名）：");
                    result = FrDev.findByType(sc.nextLine().trim());
                    break;
                case "3":
                    result = FrDev.findByStatus(FrDev.chooseDeviceStatus(sc, false));
                    break;
                case "4":
                    System.out.println("请输入关键字（id/型号/井场）：");
                    result = FrDev.fuzzyFind(sc.nextLine());
                    break;
                default:
                    System.out.println("请输入正确的数字！");
                    return;
            }
            showResults(sc, result);
        } catch (InvalidDeviceIdException | DeviceNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            FrUi.waitForEnter(sc);
        }
    }

    public static void sortDevices(Scanner sc) {
        System.out.println("1.按设备编号排序\n2.按运行状态排序\n3.按安装日期排序");
        java.util.Comparator<Equipment> comparator = switch (sc.nextLine().trim()) {
            case "1" -> devices.DeviceSort.BY_ID;
            case "2" -> devices.DeviceSort.BY_STATUS;
            case "3" -> devices.DeviceSort.BY_INSTALL_DATE;
            default -> null;
        };
        if (comparator == null) {
            System.out.println("请输入正确的数字！");
            return;
        }
        System.out.println("1.升序  2.降序");
        String direction = sc.nextLine().trim();
        while (!"1".equals(direction) && !"2".equals(direction)) {
            System.out.println("请输入 1 或 2！");
            direction = sc.nextLine().trim();
        }
        showResults(sc, devices.DeviceSort.sorted(FrDev.allDevices(), comparator, "1".equals(direction)));
    }

    public static void filterDevices(Scanner sc) {
        System.out.println("1.筛选运行中设备\n2.筛选故障设备\n3.按设备类型筛选");
        String choice = sc.nextLine().trim();
        Equipment[] result;
        switch (choice) {
            case "1": result = FrDev.findByStatus("投运中"); break;
            case "2": result = FrDev.findByStatus("故障"); break;
            case "3":
                System.out.println("请输入设备类型：");
                result = FrDev.findByType(sc.nextLine().trim());
                break;
            default:
                System.out.println("请输入正确的数字！");
                return;
        }
        showResults(sc, result);
    }

}
