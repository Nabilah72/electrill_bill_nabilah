package com.example.individual_assignment_nabilah;

public class BillCalculator {

    public static double calculateTotalCharges(double unit) {
        double total = 0;

        if (unit <= 200) {
            total = unit * 0.218;
        } else if (unit <= 300) {
            total = 200 * 0.218 + (unit - 200) * 0.334;
        } else if (unit <= 600) {
            total = 200 * 0.218 + 100 * 0.334 + (unit - 300) * 0.516;
        } else {
            total = 200 * 0.218 + 100 * 0.334 + 300 * 0.516 + (unit - 600) * 0.546;
        }

        return total;
    }

    public static double applyRebate(double total, double rebatePercent) {
        return total - (total * rebatePercent / 100);
    }
}