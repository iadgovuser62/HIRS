# Script to run the tcg_rim_tool in java

$JavaParams = @{
    FilePath =  'java'
    ArgumentList = @(
        '-jar "{0}"' -f "$PWD\tcg_rim_tool/tcg_rim_tool.jar"
        "$args"
    )
}

Start-Process @JavaParams  -NoNewWindow -Wait