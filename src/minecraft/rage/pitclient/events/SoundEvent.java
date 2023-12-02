package rage.pitclient.events;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import rage.pitclient.eventbus.event.Event;

public class SoundEvent extends Event
{
    public final SoundManager manager;
    public SoundEvent(SoundManager manager)
    {
        this.manager = manager;
    }

    public static class SoundSourceEvent extends SoundEvent
    {
        public final ISound sound;
        public final String uuid;
        public final String name;

        public SoundSourceEvent(SoundManager manager, ISound sound, String uuid)
        {
            super(manager);
            this.name = sound.getSoundLocation().getResourcePath();
            this.sound = sound;
            this.uuid = uuid;
        }
    }
}
