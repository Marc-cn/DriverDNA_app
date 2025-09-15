package org.o7planning.Ecorridor_DriverDNA.config;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.FuelRailPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.DescribeProtocolCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

import java.util.ArrayList;

/**
 * TODO put description
 */
public final class ObdConfig {

    public static ArrayList<ObdCommand> getCommands() {
        ArrayList<ObdCommand> cmds = new ArrayList<>();

        // LIVE DATA TO SHOW IN MAIN

        // Control
        //cmds.add(new ModuleVoltageCommand());
        //cmds.add(new EquivalentRatioCommand());
        cmds.add(new TimingAdvanceCommand());
        //cmds.add(new TroubleCodesCommand());
        cmds.add (new DescribeProtocolCommand());
        cmds.add(new VinCommand());
        //cmds.add (new IgnitionMonitorCommand());
        //cmds.add (new DistanceMILOnCommand());
        //cmds.add (new DistanceSinceCCCommand());
        //cmds.add (new DtcNumberCommand());
        //cmds.add (new PermanentTroubleCodesCommand());


        // Engine
        //cmds.add (new AbsoluteLoadCommand());
        cmds.add(new RPMCommand());
        cmds.add(new RuntimeCommand());
        cmds.add(new MassAirFlowCommand());
        cmds.add(new ThrottlePositionCommand());
        cmds.add (new LoadCommand());

        // Fuel

        //cmds.add(new FindFuelTypeCommand());
        //cmds.add(new ConsumptionRateCommand());
        cmds.add(new FuelLevelCommand());
        //cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
        //cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
        //cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
        //cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
        //cmds.add(new AirFuelRatioCommand());
        //cmds.add(new WidebandAirFuelRatioCommand());
        //cmds.add(new OilTempCommand());
        cmds.add (new FuelRailPressureCommand());
        //cmds.add (new FindFuelTypeCommand());



        // Pressure
        cmds.add(new BarometricPressureCommand());
        //cmds.add(new FuelPressureCommand());
        cmds.add(new FuelRailPressureCommand());
        cmds.add(new IntakeManifoldPressureCommand());

        // Temperature
        cmds.add(new AirIntakeTemperatureCommand());
        cmds.add(new AmbientAirTemperatureCommand());
        cmds.add(new EngineCoolantTemperatureCommand());

        // Misc
        cmds.add(new SpeedCommand());


        return cmds;
    }

}
