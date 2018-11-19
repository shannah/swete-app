/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.templates.page;

import com.codename1.onsen.components.OnsSplitter;
import com.codename1.onsen.components.OnsSplitterSide;
import com.codename1.onsen.components.OnsToolbarButton;

/**
 *
 * @author shannah
 */
public class PageWithSplitter extends PageTemplate {
    
    private OnsSplitterSide.Animation animation = OnsSplitterSide.Animation.Default;
    private String sideWidth="260px";
    
    public static enum SplitterType {
        Left,
        Right,
        Both;
    }
    
    public PageWithSplitter setSideWidth(String sideWidth) {
        this.sideWidth = sideWidth;
        return this;
    }
    
    public String getSideWidth() {
        return sideWidth;
    }

    public PageWithSplitter setAnimation(OnsSplitterSide.Animation animation) {
        this.animation = animation;
        return this;
    }
    
    public OnsSplitterSide.Animation getAnimation() {
        return animation;
    }
    
    /**
     * @return the startingContentPage
     */
    public PageTemplate getStartingContentPage() {
        
        return startingContentPage;
    }

    /**
     * @param startingContentPage the startingContentPage to set
     */
    public void setStartingContentPage(PageTemplate startingContentPage) {
        this.startingContentPage = startingContentPage;
    }

    /**
     * @return the startingLeftPage
     */
    public PageTemplate getStartingLeftPage() {
        return startingLeftPage;
    }

    /**
     * @param startingLeftPage the startingLeftPage to set
     */
    public void setStartingLeftPage(PageTemplate startingLeftPage) {
        this.startingLeftPage = startingLeftPage;
    }

    /**
     * @return the startingRightPage
     */
    public PageTemplate getStartingRightPage() {
        return startingRightPage;
    }

    /**
     * @param startingRightPage the startingRightPage to set
     */
    public void setStartingRightPage(PageTemplate startingRightPage) {
        this.startingRightPage = startingRightPage;
    }
    
    private PageTemplate startingContentPage, startingLeftPage, startingRightPage;
    
    
    private OnsSplitter splitter;
    
    
    public OnsSplitter getSplitter() {
        return splitter;
    }
    
    public OnsSplitterSide getSplitterSide() {
        if (startingLeftPage != null) {
            return splitter.getLeft();
        }
        if (startingRightPage != null) {
            return splitter.getRight();
        }
        return null;
    }

    private void decorateSplitterSide(OnsSplitterSide side) {
        side.setSwipeable(true);
        side.setCollapse(true);
        side.attr("width", sideWidth);
        side.setAnimation(animation);
    }
    
    @Override
    protected void initUI() {
        super.initUI();
        
        splitter = onsSplitter();
        
        if (startingLeftPage != null) {
            prepare(startingLeftPage);
            splitter.setLeft(startingLeftPage.getPage());
            decorateSplitterSide(splitter.getLeft());
        }
        
        if (startingRightPage != null) {
            prepare(startingRightPage);
            splitter.setRight(startingRightPage.getPage());
            decorateSplitterSide(splitter.getRight());
        }
        
        if (startingContentPage != null) {
            prepare(startingContentPage);
            splitter.setContent(startingContentPage.getPage());
        }
        
        getPage().add(splitter);
        
        
    }
    
    
    public static class Basic extends PageWithSplitter {
        private final SplitterType type;
        private OnsToolbarButton menuButton;
        
        public Basic(SplitterType type) {
            this.type = type;
        }
        
        private void setStartingSidePage(PageTemplate tpl) {
            if (type == SplitterType.Left) {
                setStartingLeftPage(tpl);
            } else {
                setStartingRightPage(tpl);
            }
        }
        
        @Override
        protected void initUI() {
            setStartingSidePage(new PageTemplate());
            
            
            
            setStartingContentPage(new PageWithToolbar() {
                @Override
                protected void initUI() {
                    super.initUI();
                    if (type == SplitterType.Left || type == SplitterType.Right) {
                        menuButton = onsToolbarButton();
                        if (type == SplitterType.Left) {
                            getToolbar().getLeft().add(menuButton);
                        } else {
                            getToolbar().getRight().add(menuButton);
                        }
                        menuButton.add(onsIcon("ion-navicon, material:md-menu"));
                        menuButton.on("click", e->{
                            if (type == SplitterType.Left) {
                                getSplitter().getLeft().toggle();
                            } else {
                                getSplitter().getRight().toggle();
                            }
                        });
                    }
                }
                
            });
            super.initUI();
            
            
        }
        
        
    }
    
    
    public static class Left extends Basic {
        public Left() {
            super(SplitterType.Left);
        }
    }
    
    public static class Right extends Basic {
        public Right() {
            super(SplitterType.Right);
        }
    }
    
}
