import{_ as s,c as a,a as e,o as t}from"./app-DNLxesaz.js";const l={};function p(i,n){return t(),a("div",null,n[0]||(n[0]=[e(`<h2 id="快速开始" tabindex="-1"><a class="header-anchor" href="#快速开始"><span>快速开始</span></a></h2><h3 id="_1-maven-依赖" tabindex="-1"><a class="header-anchor" href="#_1-maven-依赖"><span>1. Maven 依赖</span></a></h3><div class="language-xml line-numbers-mode" data-highlighter="prismjs" data-ext="xml" data-title="xml"><pre><code><span class="line"><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>dependency</span><span class="token punctuation">&gt;</span></span></span>
<span class="line">    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>groupId</span><span class="token punctuation">&gt;</span></span>vip.lsjscl<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>groupId</span><span class="token punctuation">&gt;</span></span></span>
<span class="line">    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>artifactId</span><span class="token punctuation">&gt;</span></span>flowboot-spring-boot-starter<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>artifactId</span><span class="token punctuation">&gt;</span></span></span>
<span class="line">    <span class="token tag"><span class="token tag"><span class="token punctuation">&lt;</span>version</span><span class="token punctuation">&gt;</span></span>1.0.0<span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>version</span><span class="token punctuation">&gt;</span></span></span>
<span class="line"><span class="token tag"><span class="token tag"><span class="token punctuation">&lt;/</span>dependency</span><span class="token punctuation">&gt;</span></span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h3 id="_2-数据库配置" tabindex="-1"><a class="header-anchor" href="#_2-数据库配置"><span>2. 数据库配置</span></a></h3><div class="language-yaml line-numbers-mode" data-highlighter="prismjs" data-ext="yml" data-title="yml"><pre><code><span class="line"><span class="token key atrule">spring</span><span class="token punctuation">:</span></span>
<span class="line">  <span class="token key atrule">datasource</span><span class="token punctuation">:</span></span>
<span class="line">    <span class="token key atrule">url</span><span class="token punctuation">:</span> jdbc<span class="token punctuation">:</span>mysql<span class="token punctuation">:</span>//localhost<span class="token punctuation">:</span>3306/workflow</span>
<span class="line">    <span class="token key atrule">username</span><span class="token punctuation">:</span> root</span>
<span class="line">    <span class="token key atrule">password</span><span class="token punctuation">:</span> root</span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h3 id="_3-集成用户系统" tabindex="-1"><a class="header-anchor" href="#_3-集成用户系统"><span>3. 集成用户系统</span></a></h3><div class="language-java line-numbers-mode" data-highlighter="prismjs" data-ext="java" data-title="java"><pre><code><span class="line"><span class="token annotation punctuation">@Component</span></span>
<span class="line"><span class="token keyword">public</span> <span class="token keyword">class</span> <span class="token class-name">CustomUserInfoProvider</span> <span class="token keyword">implements</span> <span class="token class-name">UserInfoProvider</span> <span class="token punctuation">{</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token class-name">String</span> <span class="token function">getCurrentUserId</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        <span class="token keyword">return</span> <span class="token class-name">SecurityUtils</span><span class="token punctuation">.</span><span class="token function">getCurrentUserId</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line">    </span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token class-name">String</span> <span class="token function">getCurrentUsername</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        <span class="token keyword">return</span> <span class="token class-name">SecurityUtils</span><span class="token punctuation">.</span><span class="token function">getCurrentUsername</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"><span class="token punctuation">}</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h2 id="核心功能" tabindex="-1"><a class="header-anchor" href="#核心功能"><span>核心功能</span></a></h2><ul><li>图形化流程设计器</li><li>流程版本管理</li><li>多种任务处理方式</li><li>待办/已办任务查询</li><li>流程历史记录追踪</li><li>自定义表单集成</li></ul><h2 id="技术支持" tabindex="-1"><a class="header-anchor" href="#技术支持"><span>技术支持</span></a></h2><ul><li>问题反馈：<a href="https://gitee.com/zhang_xing_ju/dk-workflow/issues" target="_blank" rel="noopener noreferrer">Gitee Issues</a></li><li>交流：QQ 1533195362</li></ul><h2 id="许可证" tabindex="-1"><a class="header-anchor" href="#许可证"><span>许可证</span></a></h2><p>本项目采用 MIT 协议开源。</p>`,13)]))}const o=s(l,[["render",p],["__file","index.html.vue"]]),u=JSON.parse('{"path":"/","title":"","lang":"zh-CN","frontmatter":{"home":true,"heroImage":"/images/logo.png","heroText":"登科流程引擎","tagline":"一个基于 Spring Boot 的轻量级工作流引擎","actions":[{"text":"快速开始 →","link":"/guide/getting-started.html","type":"primary"},{"text":"项目简介","link":"/guide/","type":"secondary"}],"features":[{"title":"简单易用","details":"提供图形化流程设计器，零代码即可完成流程定义"},{"title":"轻量灵活","details":"基于 Spring Boot 开发，易于集成，支持自定义扩展"},{"title":"功能完善","details":"支持流程版本管理、任务处理、流程追踪等核心功能"}],"footer":"MIT Licensed | Copyright © 2024 登科流程引擎"},"headers":[{"level":2,"title":"快速开始","slug":"快速开始","link":"#快速开始","children":[{"level":3,"title":"1. Maven 依赖","slug":"_1-maven-依赖","link":"#_1-maven-依赖","children":[]},{"level":3,"title":"2. 数据库配置","slug":"_2-数据库配置","link":"#_2-数据库配置","children":[]},{"level":3,"title":"3. 集成用户系统","slug":"_3-集成用户系统","link":"#_3-集成用户系统","children":[]}]},{"level":2,"title":"核心功能","slug":"核心功能","link":"#核心功能","children":[]},{"level":2,"title":"技术支持","slug":"技术支持","link":"#技术支持","children":[]},{"level":2,"title":"许可证","slug":"许可证","link":"#许可证","children":[]}],"git":{"updatedTime":1739757856000,"contributors":[{"name":"zhangxingju","username":"zhangxingju","email":"1533195362@qq.com","commits":2}]},"filePathRelative":"README.md"}');export{o as comp,u as data};
