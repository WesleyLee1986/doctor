package database.model;

import java.util.List;

public enum VisitDate {
    /**
     * 二进制标识：0000 0000 0000 0000
     * 从右往左以此表示：周一上午，周一下午，周二上午，周二下午。。。周日上午，周日下午
     * 如：0x0001（0000 0000 0000 0001） 表示周一上午出诊
     * 0x0002（0000 0000 0000 0010）表示周一下午出诊
     * 0x0003（0000 0000 0000 0011）表示周一上午和下午都出诊
     * 0x0009（0000 0000 0000 0101）表示周二上午和周一上午出诊
     */
    MON_AM(0x0001, "周一上午"),
    MON_PM(0x0002, "周一下午"),
    TUE_AM(0x0004, "周二上午"),
    TUE_PM(0x0008, "周二下午"),
    WED_AM(0x0010, "周三上午"),
    WED_PM(0x0020, "周三下午"),
    THUR_AM(0x0040, "周四上午"),
    THUR_PM(0x0080, "周四下午"),
    FRI_AM(0x0100, "周五上午"),
    FRI_PM(0x0200, "周五下午"),
    SAT_AM(0x0400, "周六上午"),
    SAT_PM(0x0800, "周六下午"),
    SUN_AM(0x1000, "周日上午"),
    SUN_PM(0x2000, "周日下午");

    private final int val;
    private final String name;

    VisitDate(int value, String name) {
        this.val = value;
        this.name = name;
    }

    public void getVisitDate(int dateVal) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (VisitDate date : values()) {
            if ((dateVal & date.val) == date.val) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append(",");
                }
                builder.append(date.name);
            }
        }
    }

    public VisitDate getVisitData(String name) {
        if (null == name || "".equals(name)) {
            return null;
        }

        for (VisitDate date : values()) {
            if (date.name.equals(name)) {
                return date;
            }
        }

        return null;
    }

    public int getVisitDateVal(List<String> vistName) {
        int dateVal = 0;
        for (String name : vistName) {
            VisitDate date = getVisitData(name);
            if (null != date) {
                dateVal |= date.val;
            }
        }

        return dateVal;
    }
}
