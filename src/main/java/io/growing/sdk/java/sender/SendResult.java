package io.growing.sdk.java.sender;

public class SendResult {
    public enum State {
        SUCCESS,
        NETWORK_FAILURE,
        EXCEPTION_FAILURE,
        UNKNOWN_ERROR
    }
    private State state;
    private String msg;

    public SendResult(State state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    public State getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "SendResult{" +
                "state=" + state +
                ", msg='" + msg + '\'' +
                '}';
    }
}
