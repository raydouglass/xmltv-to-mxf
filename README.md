# XMLTV to MXF
This is utility which takes a file in XMLTV format (http://xmltv.org) and converts it to Windows Media Center Guide Listings Format or MXF (https://msdn.microsoft.com/en-us/library/windows/desktop/dd776338.aspx).

## What does it do
This program supports many of the features that XMLTV & MXF have in common.

This program includes a custom written MXF XSD file which was created using Microsoft's reference documentation along with numerous samples. This means the converted XMLTV file can be validated before attempting to load it into WMC which allows for better error messages.

It supports marking HD, detecting repeats, and identifying uniqueness (in many cases). This helps to ensure WMC can be configured to record when you want it to - IE. HD or New shows only.

## What this does not do
This does not download any XMLTV data. Try using http://sourceforge.net/projects/xmltv/files/ or http://mc2xml.hosterbox.net/

This does not load the resulting MXF file into WMC. Use `C:\Windows\ehome\loadmxf.exe` for that.

This does not reconfigure or remap channels/listings. You will need to find another program or do this manually.

Affiliate data, guide images (such as channel logos), people credits (actor/director/producer/etc), and keywords are NOT currently loaded.

## Usage
1. Obtain XMLTV data
  1. http://sourceforge.net/projects/xmltv/files/
  2. http://mc2xml.hosterbox.net/
2. `java -jar xmltv-parser-<version>.jar xmltv.xml`
3. Open Command Prompt and run: `C:\Windows\ehome\loadmxf -i mxf.xml`
4. Remap channels
  1. Open WMC
  2. Go to Settings->TV->Guide->Edit Channels
  3. Select a Channel
  4. Go to 'Edit Listings'
  5. Select 'Mainlineup'
  6. Repeat for every channel :(
5. Reindexing WMC - https://msdn.microsoft.com/en-us/library/windows/desktop/dd776338.aspx#reindexing_guide_data_after_load_new_guide_data__ykic
  1. This ensures you can search through the new data properly
6. Fix recordings if needed
	

Note: it is likely that any previously scheduled recordings will NOT work after loading this data. This is because the IDs for Series and Programs are not the same as normal WMC EPG data. However, repeated loads of the same XMLTV data source using this program should result in consistent data. In other words, runs of this program on XMLTV data from the same source should have consistent series IDs and even program IDs if the source does.