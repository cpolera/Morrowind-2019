@echo off
echo Cleaning plugins from Morrowind-2019 guide...
echo Cleaning process started %date% ~ %time% > cleaning.log
echo.

call :clean "wl_SolstheimOverhaul_v1.esm"
call :clean "correctUV Ore Replacer 1.0.esp"
call :clean "What Thieves Guild.ESP"
call :clean "LGNPC_TelMora_v1_30.esp"
call :clean "LGNPC_Khuul_v2_21.esp"
call :clean "Less_Generic_Bloodmoon.esp"
call :clean "Less_Generic_Nerevarine.esp"
call :clean "LGNPC_SecretMasters_v1_30.esp"
call :clean "LGNPC_IndarysManor_v1_51.esp"
call :clean "LGNPC_PaxRedoran_v1_20.esp"
call :clean "Bloated Caves.esp"
call :clean "Graphic Herbalism.esp"
call :clean "Apel's_Asura_Coast_Fix.esp"
call :clean "Vurt's BC Tree Replacer II.ESP"
call :clean "Graphic Herbalism TotSP.esp"
call :clean "VoicedVivec.ESP"
call :clean "YaketyYagrum.ESP"
call :clean "Better Clothes_v1.1_nac.esp"
call :clean "UFR_v3dot2.esp"
call :clean "Better Morrowind Armor.esp"
call :clean "Illy's Hot Pots.ESP"

echo.
echo Finished, read log output in cleaning.log
pause

:clean
if Exist "%~1" (
	tes3cmd clean "%~1" >> cleaning.log
	if Exist "Clean_%~1" (
		move "%~1" "tes3cmddirty\%~1" > NUL
		ren "Clean_%~1" "%~1"
	) else echo Skipping %~1 ^(already clean^)
) else echo Skipping %~1 ^(unable to find file^)
exit /b