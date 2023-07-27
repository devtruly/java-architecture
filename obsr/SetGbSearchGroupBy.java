package com.simpledesign.ndms.common.obsr;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SetGbSearchGroupBy {
    public final DateTimeExpression<LocalDateTime> dateTime;
    public final NumberExpression<Integer> year;
    public final NumberExpression<Integer> month;
    public final NumberExpression<Integer> day;
    public final NumberExpression<Integer> hour;
    public final NumberExpression<Integer> minute10;
    public final NumberExpression<Integer> minute;
    public final GbSearchCode gbSearchCode;

    public SetGbSearchGroupBy(StringPath stringPath, GbSearchCode gbSearchCode) {
        dateTime = Expressions.dateTimeTemplate(LocalDateTime.class,
                "str_to_date({0}, '%Y%m%d%H%i%s')",
                stringPath);
        year = Expressions.numberTemplate(Integer.class, "year({0})", dateTime);
        month = Expressions.numberTemplate(Integer.class, "month({0})", dateTime);
        day = Expressions.numberTemplate(Integer.class, "day({0})", dateTime);
        hour = Expressions.numberTemplate(Integer.class, "hour({0})", dateTime);
        minute10 = Expressions.numberTemplate(Integer.class, "floor(minute({0}) / 10)", dateTime);
        minute = Expressions.numberTemplate(Integer.class, "minute({0})", dateTime);
        this.gbSearchCode = gbSearchCode;
    }

    public List<Expression<?>> getGroupByList() {
        List<Expression<?>> groupByList = new ArrayList<>();
        if (!gbSearchCode.equals(GbSearchCode.raw)) {
            groupByList.add(year);
            groupByList.add(month);
            groupByList.add(day);
            if (!gbSearchCode.equals(GbSearchCode.avg1d)) {
                groupByList.add(hour);
            }
            if (gbSearchCode.equals(GbSearchCode.avg10m)) {
                groupByList.add(minute10);
            }
            if (gbSearchCode.equals(GbSearchCode.avg1m) || gbSearchCode.equals(GbSearchCode.avg1s)) {
                groupByList.add(minute);
            }
        }
        return groupByList;
    }
}
