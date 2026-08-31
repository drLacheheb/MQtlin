; -----------------------------------------------------------------------------
; MQtlin - Modern Dark Setup Wizard Script (Inno Setup 6)
; -----------------------------------------------------------------------------

#define MyAppName "MQtlin"
#ifndef MyAppVersion
#define MyAppVersion "0.1.0-beta.1"
#endif
#define MyAppPublisher "drlacheheb"
#define MyAppURL "https://github.com/drlacheheb/mqtlin"
#define MyAppExeName "MQtlin.exe"
#define MyAppAssocName "MQtlin Connection Profile"
#define MyAppAssocExt ".mqtlin"
#define MyAppAssocKey "MQtlin.Profile"

[Setup]
; Unique application GUID for clean upgrades and uninstall tracking
AppId={{1F866712-E38F-7744-9350-C05793AD7902}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName} v{#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
UninstallDisplayIcon={app}\{#MyAppExeName}
ChangesAssociations=yes
DisableProgramGroupPage=yes

; Modern Dark Theme UI Styling
WizardStyle=modern dark
WizardResizable=no

; Allow installation for both regular users and admins
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog

; Seamless Upgrade and In-Place Update Settings
UsePreviousAppDir=yes
UsePreviousTasks=yes
CloseApplications=yes
RestartApplications=yes

; Output configuration
OutputBaseFilename=MQtlin-Setup-{#MyAppVersion}
OutputDir=..\..\desktopApp\build\compose\binaries\main\setup
Compression=lzma2/ultra64
SolidCompression=yes
SetupIconFile=..\..\desktopApp\src\main\resources\icons\icon.ico
WizardSmallImageFile=wizard_small.bmp

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: checkedonce
Name: "startmenuicon"; Description: "Create a Start Menu shortcut"; GroupDescription: "{cm:AdditionalIcons}"; Flags: checkedonce

[Files]
Source: "..\..\desktopApp\build\compose\binaries\main\app\MQtlin\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Registry]
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocExt}\OpenWithProgids"; ValueType: string; ValueName: "{#MyAppAssocKey}"; ValueData: ""; Flags: uninsdeletevalue
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}"; ValueType: string; ValueName: ""; ValueData: "{#MyAppAssocName}"; Flags: uninsdeletekey
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppExeName},0"
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: startmenuicon
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

