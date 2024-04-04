package charty;

/**
 * Serialization proxies for Chart types.
 */
public interface SerializableChartProxy extends java.io.Serializable {
    /**
     * Creates a chart.
     *
     * @return The newly created chart.
     */
    Chart intoChart();
}
