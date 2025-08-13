package io.github.nahuel92;

import io.mavsdk.action.Action;
import io.mavsdk.mission.Mission;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Singleton
public class Poc {
    private final MavsdkProps props;

    Poc(final MavsdkProps props) {
        this.props = props;
    }

    @EventListener
    void a(final StartupEvent ev) {
        final var cycle = new ArrayList<Mission.MissionItem>();
        cycle.add(generateMissionItem(47.398039859999997, 8.5455725400000002));
        cycle.add(generateMissionItem(47.398036222362471, 8.5450146439425509));
        cycle.add(generateMissionItem(47.397825620791885, 8.5450092830163271));
        cycle.add(generateMissionItem(47.397832880000003, 8.5455939999999995));
        final var missionPlan = new Mission.MissionPlan(cycle);

        final var s = new io.mavsdk.System(props.hostname(), props.port());

        s.getAction().arm()
                .andThen(
                        s.getAction().takeoff()
                                .doOnError(System.out::println)
                )
                .delay(10, TimeUnit.SECONDS)
                .andThen(
                        s.getAction()
                                .gotoLocation(47.398039859999997, 8.5455725400000002, 10F, Float.NaN)
                                .doOnError(System.out::println)
                )
                //.andThen(s.getAction().returnToLaunch())
                .subscribe();

        /*
        s.getAction().arm()
                .andThen(s.getAction().takeoff())
                .delay(10, TimeUnit.SECONDS)
                .andThen(s.getMission().setReturnToLaunchAfterMission(true))
                .andThen(s.getMission().uploadMission(missionPlan))
                //.andThen(s.getAction().land())
                .subscribe();
         */
    }

    private static Mission.MissionItem generateMissionItem(double latitudeDeg, double longitudeDeg) {
        return new Mission.MissionItem(
                latitudeDeg,
                longitudeDeg,
                10f,
                10f,
                true,
                Float.NaN,
                Float.NaN,
                Mission.MissionItem.CameraAction.NONE,
                Float.NaN,
                Double.NaN,
                Float.NaN,
                Float.NaN,
                Float.NaN
        );
    }
}

@ConfigurationProperties("mavsdk")
record MavsdkProps(String hostname, int port) {
}