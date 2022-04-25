package frc.robot.Communication;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Network {
    private NetworkTable table;

    public Network(String key) {
        table = NetworkTableInstance.getDefault().getTable(key);

    }
}
