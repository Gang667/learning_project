package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Article;
import com.example.exception.CustomException;
import com.example.mapper.ArticleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    @Resource
    private ArticleMapper articleMapper;


    public void add(Article article) {
        article.setTime(DateUtil.now()); //获取当前最新的时间的字符串
        articleMapper.insert(article);
    }

    public void update(Article article) {
        articleMapper.updateById(article);
    }

    public void deleteById(Integer id) {
        articleMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for(Integer id : ids) {
            this.deleteById(id);
        }
    }

    public List<Article> selectAll(Article article) {
        List<Article> list = articleMapper.selectAll(article);
        return list;
    }

    public Article selectById(Integer id) {
        Article article = articleMapper.selectById(id);
        return article;
    }

    public PageInfo<Article> selectByPage(Article article, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> list = articleMapper.selectAll(article);
        return PageInfo.of(list);
    }

    public Integer selectCountByDate(String date) {
        return articleMapper.selectCountByDate(date);
    }
}
