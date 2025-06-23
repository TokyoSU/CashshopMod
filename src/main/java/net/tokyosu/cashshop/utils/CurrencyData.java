package net.tokyosu.cashshop.utils;

import com.eliotlash.mclib.utils.MathUtils;

public class CurrencyData {
    public long gold;
    public long silver;
    public long copper;

    public CurrencyData(long gold, long silver, long copper) {
        this.copper = copper;
        this.silver = silver;
        this.gold = (long)MathUtils.clamp(gold, 0, 99999999);
    }

    public long getTotal() {
        return gold * 10000 + silver * 100 + copper;
    }

    public boolean isGreaterOrEqual(CurrencyData other) {
        return this.getTotal() >= other.getTotal();
    }

    public boolean isGreaterThan(CurrencyData other) {
        return this.getTotal() > other.getTotal();
    }

    public boolean isLessOrEqual(CurrencyData other) {
        return this.getTotal() <= other.getTotal();
    }

    public boolean isLessThan(CurrencyData other) {
        return this.getTotal() < other.getTotal();
    }

    public boolean isEqualTo(CurrencyData other) {
        return this.getTotal() == other.getTotal();
    }

    public boolean isNotEqualTo(CurrencyData other) {
        return this.getTotal() != other.getTotal();
    }

    public void set(CurrencyData other) {
        long totalResult = other.getTotal();
        this.gold = totalResult / 10000;
        this.gold = (long)MathUtils.clamp(gold, 0, 99999999);
        long remainder = totalResult % 10000;
        this.silver = remainder / 100;
        this.copper = remainder % 100;
    }

    public void add(CurrencyData other) {
        long totalThis = this.getTotal();
        long totalOther = other.getTotal();
        long totalResult = totalThis + totalOther;
        this.gold = totalResult / 10000;
        this.gold = (long)MathUtils.clamp(this.gold, 0, 99999999);
        long remainder = totalResult % 10000;
        this.silver = remainder / 100;
        this.copper = remainder % 100;
    }

    // Subtraction: returns new CurrencyData as difference
    // Note: result may have negative gold if this < other
    public void subtract(CurrencyData other) {
        long total1 = this.getTotal();
        long total2 = other.getTotal();
        long total = total1 - total2;

        // Prevent negative result
        if (total < 0) {
            total = 0;
        }

        this.gold = total / 10000;
        this.gold = (long)MathUtils.clamp(gold, 0, 99999999);
        long remainder = total % 10000;
        this.silver = remainder / 100;
        this.copper = remainder % 100;
    }

    public CurrencyData copy() {
        return new CurrencyData(this.gold, this.silver, this.copper);
    }

    @Override
    public String toString() {
        return gold + "g " + silver + "s " + copper + "c";
    }
}
