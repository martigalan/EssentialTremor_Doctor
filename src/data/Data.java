package data;

public class Data {
    /**
     * Acceleration data taken from Bitalino
     */
    private ACC acc;
    /**
     * EMG data taken from Bitalino
     */
    private EMG emg;

    /**
     * Constructor
     * @param acc acceleration data
     * @param emg emg data
     */
    public Data(ACC acc, EMG emg) {
        this.acc = acc;
        this.emg = emg;
    }

    public ACC getAcc() {
        return acc;
    }

    public void setAcc(ACC acc) {
        this.acc = acc;
    }

    public EMG getEmg() {
        return emg;
    }

    public void setEmg(EMG emg) {
        this.emg = emg;
    }
}
