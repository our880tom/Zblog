package com.zblog.biz.aop;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zblog.biz.CategoryManager;
import com.zblog.biz.PostManager;
import com.zblog.core.dal.entity.Post;
import com.zblog.core.plugin.MapContainer;
import com.zblog.core.util.NumberUtils;
import com.zblog.core.util.constants.OptionConstants;
import com.zblog.core.util.constants.PostConstants;
import com.zblog.core.util.constants.WebConstants;
import com.zblog.service.LinkService;
import com.zblog.service.OptionsService;
import com.zblog.service.TagService;
import com.zblog.service.freemarker.FreeMarkerUtils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;

/**
 * 静态化组件
 * 
 * @author zhou
 * 
 */
@Component
public class StaticTemplate{
  private static final Logger logger = LoggerFactory.getLogger(StaticTemplate.class);
  @Autowired
  private CategoryManager categoryManager;
  @Autowired
  private PostManager postManager;
  @Autowired
  private LinkService linksService;
  @Autowired
  private OptionsService optionsService;
  @Autowired
  private TagService tagService;

  /**
   * 静态化导航栏
   */
  public void staticHeader(){
    MapContainer map = new MapContainer();
    map.put("domain", WebConstants.getDomain());
    map.put("title", optionsService.getOptionValue(OptionConstants.TITLE));
    map.put("subtitle", optionsService.getOptionValue(OptionConstants.SUBTITLE));
    map.put("categorys", categoryManager.listAsTree());
    map.put("pages", postManager.listPageAsTree());

    FreeMarkerUtils.genHtml("/common/header.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/header.html"), map);

    logger.info("staticHeader");
  }

  /**
   * 静态化标签云
   */
  private void staticCloudTags(){
    MapContainer map = new MapContainer();
    map.put("tags", tagService.list());
    map.put("domain", WebConstants.getDomain());
    try{
      BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
      TemplateHashModel thm = (TemplateHashModel) wrapper.getStaticModels().get(NumberUtils.class.getName());
      map.put("NumberUtils", thm);
    }catch(Exception e){
      logger.error("staticCloudTags init NumberUtils error", e);
    }

    FreeMarkerUtils.genHtml("/common/tagcloud.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/tagcloud.html"), map);
    logger.info("staticCloudTags");
  }

  /**
   * 静态化友情链接
   */
  public void staticLinks(){
    MapContainer map = new MapContainer();
    map.put("links", linksService.list());

    FreeMarkerUtils.genHtml("/common/link.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
        + "/common/link.html"), map);
    logger.info("staticLinks");
  }

  /**
   * 静态化文章,同时静态化最近发表or顶部导航页面栏
   * 
   * @param post
   */
  public void postInsertOrUpdate(Post post){
    staticRecentOrHeader(PostConstants.TYPE_POST.equals(post.getType()));
  }

  public void postRemove(String postid, String postType){
    staticRecentOrHeader(PostConstants.TYPE_POST.equals(postType));
  }

  /**
   * 静态化最近发表或者静态还顶部导航
   * 
   * @param ispost
   */
  private void staticRecentOrHeader(boolean ispost){
    if(ispost){
      MapContainer param = new MapContainer("domain", WebConstants.getDomain());
      param.put("posts", postManager.listRecent(10));
      FreeMarkerUtils.genHtml("/common/recent.html", new File(WebConstants.APPLICATION_PATH, WebConstants.PREFIX
          + "/common/recent.html"), param);
      logger.info("staticRecent");

      staticCloudTags();
    }else{
      staticHeader();
    }
  }

}
