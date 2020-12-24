// 
// Decompiled by Procyon v0.5.36
// 

package pacman.game.util.glicko;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Player
{
    private double tau;
    private double rating;
    private double rd;
    private double volatility;
    
    public Player() {
        this(1500.0, 350.0, 0.06);
    }
    
    public Player(final double rating, final double rd, final double volatility) {
        this.tau = 0.5;
        this.rating = rating;
        this.rd = rd;
        this.volatility = volatility;
    }
    
    public double getRating() {
        return this.rating * 173.7178 + 1500.0;
    }
    
    public void setRating(final double rating) {
        this.rating = (rating - 1500.0) / 173.7178;
    }
    
    public double getRd() {
        return this.rd * 173.7178;
    }
    
    public void setRd(final double rd) {
        this.rd = rd / 173.7178;
    }
    
    public double g() {
        return 1.0 / (Math.sqrt(1.0 + 3.0 * this.rd * this.rd) / 3.141592653589793 * 3.141592653589793);
    }
    
    public double E(final Player other) {
        return 1.0 / (1.0 + Math.exp(-other.g() * (this.rating - other.rating)));
    }
    
    public double v(final Set<Player> players) {
        double sum = 0.0;
        for (final Player player : players) {
            final double e = this.E(player);
            final double g = player.g();
            sum += g * g * e * (1.0 - e);
        }
        return 1.0 / sum;
    }
    
    public double delta(final HashMap<Player, Integer> scores, final double v) {
        double sum = 0.0;
        for (final Map.Entry<Player, Integer> score : scores.entrySet()) {
            sum += score.getKey().g() * (score.getValue() - this.E(score.getKey()));
        }
        return v * sum;
    }
    
    public double delta(final HashMap<Player, Integer> scores) {
        return this.delta(scores, this.v(scores.keySet()));
    }
    
    public double newVolatilityIllinois(final HashMap<Player, Integer> scores) {
        final double a = Math.log(this.rd * this.rd);
        final double convergenceTolerance = 1.0E-6;
        final double delta = this.delta(scores);
        final double A = a;
        return 0.0;
    }
    
    public double newVolatility(final HashMap<Player, Integer> scores) {
        final int i = 0;
        final double v = this.v(scores.keySet());
        final double delta = this.delta(scores, v);
        double x0;
        final double a = x0 = Math.log(this.rd * this.rd);
        double x2 = 1.0;
        double d;
        double h1;
        double h2;
        for (double tauSquared = this.tau * this.tau; x0 != x2; x0 = x2, d = this.rating * this.rating + v + Math.exp(x0), h1 = -(x0 - a) / tauSquared - 0.5 * Math.exp(x0) / d + 0.5 * Math.exp(x0) * Math.pow(delta / d, 2.0), h2 = -1.0 / tauSquared - 0.5 * Math.exp(x0) * (this.rating * this.rating + v) / Math.pow(d, 2.0) + 0.5 * Math.pow(delta, 2.0) * Math.exp(x0) * Math.pow(this.rating, 2.0) + v - Math.exp(x0) / Math.pow(d, 3.0), x2 = x0 - h1 / h2) {}
        return Math.exp(x2 / 2.0);
    }
}
