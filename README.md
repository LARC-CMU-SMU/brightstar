# brightstar
Android app to collect bunch of stat from devices

writes to file
`Internal storage/Android/data/com.example.brightstar/files/test.txt`

in format
`timestamp,brightness,illuminance`

## notes

get screen brightness `adb shell settings get system screen_brightness`

set screen brightness `adb shell settings put system screen_brightness 100`
