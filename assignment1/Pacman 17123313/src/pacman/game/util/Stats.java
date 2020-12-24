// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.util;

public class Stats
{
    private double average;
    private double sum;
    private double sumsq;
    private double sd;
    private int n;
    private double min;
    private double max;
    private boolean computed;
    private String description;
    private long msTaken;
    
    public Stats(final String description) {
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        this.description = description;
    }
    
    public Stats(final long msTaken, final String description, final double max, final double min, final int n, final double sumsq, final double sum) {
        this.min = Double.POSITIVE_INFINITY;
        this.max = Double.NEGATIVE_INFINITY;
        this.msTaken = msTaken;
        this.description = description;
        this.max = max;
        this.min = min;
        this.n = n;
        this.sumsq = sumsq;
        this.sum = sum;
    }
    
    public static void main(final String[] args) {
        final Stats stats = new Stats("");
        stats.add(1.0);
        stats.add(2.0);
        stats.add(2.0);
        stats.add(2.0);
        stats.add(2.0);
        stats.add(2.0);
        System.out.println(stats);
    }
    
    public void add(final double observation) {
        ++this.n;
        this.sum += observation;
        this.sumsq += observation * observation;
        if (observation < this.min) {
            this.min = observation;
        }
        if (observation > this.max) {
            this.max = observation;
        }
        this.computed = false;
    }
    
    private void compute() {
        if (!this.computed) {
            this.average = this.sum / this.n;
            double num = this.sumsq - this.n * this.average * this.average;
            if (num < 0.0) {
                num = 0.0;
            }
            this.sd = Math.sqrt(num / (this.n - 1));
            this.computed = true;
        }
    }
    
    public void add(final Stats other) {
        this.n += other.n;
        this.sum += other.sum;
        this.sumsq += other.sumsq;
        if (other.min < this.min) {
            this.min = other.min;
        }
        if (other.max > this.max) {
            this.max = other.max;
        }
        this.computed = false;
        this.msTaken += other.msTaken;
    }
    
    public double getAverage() {
        if (!this.computed) {
            this.compute();
        }
        return this.average;
    }
    
    public int getN() {
        return this.n;
    }
    
    public double getSum() {
        return this.sum;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public double getStandardDeviation() {
        if (!this.computed) {
            this.compute();
        }
        return this.sd;
    }
    
    public double getStandardError() {
        if (!this.computed) {
            this.compute();
        }
        return this.sd / Math.sqrt(this.n);
    }
    
    public long getMsTaken() {
        return this.msTaken;
    }
    
    public void setMsTaken(final long msTaken) {
        this.msTaken = msTaken;
    }
    
    public double getSumsq() {
        return this.sumsq;
    }
    
    public double getSd() {
        if (!this.computed) {
            this.compute();
        }
        return this.sd;
    }
    
    public boolean isComputed() {
        return this.computed;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public String toString() {
        if (!this.computed) {
            this.compute();
        }
        return "Stats{Desc=" + this.description + ", average=" + this.average + ", sum=" + this.sum + ", sumsq=" + this.sumsq + ", sd=" + this.sd + ", n=" + this.n + ", min=" + this.min + ", max=" + this.max + ", stdErr=" + this.getStandardError() + ", ms=" + this.msTaken + '}';
    }
}
