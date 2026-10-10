package interfaces;

/**
 * 统计报表能力（FR-RPT）。
 * 各类设备/报警/运维模块各自实现，报表模块面向接口汇总，
 * 新增设备类型时报表代码无需改动（开闭原则）。
 */
public interface Reportable {

    /** 生成该对象的一行统计信息，供报表模块拼表。 */
    String toReportLine();

    /** 该对象应归入的报表分类名（如 "生产设备"、"监测设备"）。 */
    String reportCategory();
}