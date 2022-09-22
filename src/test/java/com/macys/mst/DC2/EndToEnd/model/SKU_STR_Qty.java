
package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class SKU_STR_Qty implements Comparable{
    private int Str_nbr;
    private int qty;


    public int getStr_nbr() {
        return Str_nbr;
    }

    public void setStr_nbr(int str_nbr) {
        Str_nbr = str_nbr;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        SKU_STR_Qty other = (SKU_STR_Qty) o;
        return this.getQty()- ((SKU_STR_Qty) o).getQty();
    }
}
