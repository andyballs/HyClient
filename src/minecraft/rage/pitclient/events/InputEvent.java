package rage.pitclient.events;

import rage.pitclient.eventbus.event.Event;

public class InputEvent extends Event {
    public static class MouseInputEvent extends InputEvent {}
    public static class KeyInputEvent extends InputEvent {}
}
