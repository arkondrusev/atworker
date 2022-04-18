set dir=D:\Users\user\Desktop\arbitrader\stand\atworker

ATTRIB +H %dir%\persistent_files
FOR /D %%i IN ("%dir%\*") DO (
	RD /S /Q "%%i"
)
DEL /Q "%dir%\*.*"
ATTRIB -H %dir%\persistent_files

xcopy /s %cd% %dir%

DEL %dir%\%~nx0