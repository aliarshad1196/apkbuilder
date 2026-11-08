# HDMI 1 startup behavior

After Android sends BOOT_COMPLETED, the helper waits 45 seconds, opens the MStar input source selector, waits 4 seconds, sends DOWN six times with 250 ms spacing, and sends ENTER. This reproduces the sequence verified manually over ADB on the MST038B TV.
