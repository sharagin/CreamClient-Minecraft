@echo off
if "%~1"=="" (
  echo Drag and drop an .mp4 or .mp3 file onto this script to convert to 16-bit 44100Hz .wav!
  pause
  exit /b
)
ffmpeg -y -i "%~1" -vn -acodec pcm_s16le -ar 44100 -ac 2 "%~dpn1.wav"
echo Done: %~dpn1.wav
pause
