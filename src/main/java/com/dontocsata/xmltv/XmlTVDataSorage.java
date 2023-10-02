package com.dontocsata.xmltv;

import java.util.Collection;

import com.dontocsata.xmltv.model.XmlTvChannel;
import com.dontocsata.xmltv.model.XmlTvProgram;

public interface XmlTVDataSorage
{
   public void save(XmlTvChannel channel) ;

   public XmlTvChannel getChannel(String id) ;

   public Collection<XmlTvChannel> getChannels();

   public void save(XmlTvProgram program) ;

   public Collection<XmlTvProgram> getXmlTvPrograms() ;

}
