package io.growing.sdk.java.ab;

public interface ABTestCallback {

    void onABExperimentReceived(ABExperiment experiment);

    void onABExperimentFailed(Exception error);
}
