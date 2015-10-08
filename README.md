# XMLTV to MXF
This is utility which takes a file in XMLTV format (http://xmltv.org) and converts it to Windows Media Center Guide Listings Format or MXF (https://msdn.microsoft.com/en-us/library/windows/desktop/dd776338.aspx).

## What this does not do
This does not download or locate any XMLTV data. I recommend using http://mc2xml.hosterbox.net/

This does not load the resulting MXF file into WMC. Use C:\Windows\ehome\loadmxf.exe for that.

This does not reconfigure or remap channels. You will need to find another program or do this manually.

## What does it do
This program supports many of the features that XMLTV & MXF have in common.

It supports marking HD, detecting repeats, and identifying uniqueness (in many cases). This helps to ensure WMC can be configured to record when you want it to - IE. HD or New shows only.

## Usage
1. Obtain XMLTV data
2. java -jar xmltv-parse-<version>.jar xmltv.xml
3. loadmxf -i mxf.xml
4. Remap channels if needed
5. Fix recordings if needed

Note: it is likely that any previously scheduled recordings will NOT work after loading this data. This is because the IDs for Series and Programs are not the same as normal WMC EPG data. However, repeated loads of the same XMLTV data source using this program will result in consistent data. In other words, runs of this program on XMLTV data from the same source will have consistent series IDs and even program IDs.