/** 
 * PrimeFaces Opencell Layout
 */
var Opencell = {
  
    init: function() {
        this.menuWrapper = $('#layout-menu-cover');
        this.menu = this.menuWrapper.find('ul.layout-menu');
        this.menulinks = this.menu.find('a.menulink');
        this.menuButton = $('#menu-button');
        this.topmenuButton = $('#topmenu-button');
        this.topbarMenu = $('#topbar-menu');
        this.expandedMenuitems = this.expandedMenuitems||[];

        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.menuButton.on('click',function() {
            $this.menuButtonClick = true;
            
            if($this.menuWrapper.hasClass('active')){
                $this.menuButton.removeClass('active');
                $this.menuWrapper.removeClass('active');
            }
            else{
                $this.menuButton.addClass('active');
                $this.menuWrapper.addClass('active');
                $this.topbarMenu.removeClass('active');
                $this.topmenuButton.removeClass('active');
            }
            
            // Equalizing of height of first level nodes of main menu
            equalheight('.WideMenu .layout-menu > li');
        });
        
        this.topmenuButton.on('click',function() {  
            $this.topmenuButtonClick = true;
            
            if($this.topbarMenu.hasClass('active')){
                $this.topbarMenu.removeClass('active');
                $this.topmenuButton.removeClass('active');
            }
            else{
                $this.topbarMenu.addClass('active');
                $this.topmenuButton.addClass('active');
                $this.menuButton.removeClass('active');
                $this.menuWrapper.removeClass('active');
            }
        });
       
        this.menulinks.on('click',function(e) {
            var menuitemLink = $(this),
            menuitem = menuitemLink.parent();
            
            if(menuitem.hasClass('active-menu-parent')) {
                menuitem.removeClass('active-menu-parent');
                menuitemLink.removeClass('active-menu').next('ul').removeClass('active-menu');
                $this.removeMenuitem(menuitem.attr('id'));
            }
            else {
                var activeSibling = menuitem.siblings('.active-menu-parent');
                if(activeSibling.length) {
                    activeSibling.removeClass('active-menu-parent');
                    $this.removeMenuitem(activeSibling.attr('id'));

                    activeSibling.find('ul.active-menu,a.active-menu').removeClass('active-menu');
                    activeSibling.find('li.active-menu-parent').each(function() {
                        var menuitem = $(this);
                        menuitem.removeClass('active-menu-parent');
                        $this.removeMenuitem(menuitem.attr('id'));
                    });
                }

                menuitem.addClass('active-menu-parent');
                menuitemLink.addClass('active-menu').next('ul').addClass('active-menu');
                $this.addMenuitem(menuitem.attr('id'));
            }

            if(menuitemLink.next().is('ul')) {
                e.preventDefault();
            }
            else {
                $this.menuButton.removeClass('active');
                $this.menuWrapper.removeClass('active');
            }
            // Equalizing of height of first level nodes of main menu
            equalheight('.WideMenu .layout-menu > li');
            
            $this.saveMenuState();
        });
        
        this.menuWrapper.clickOff(function(e) {
            if($this.menuButtonClick) {
                $this.menuButtonClick = false;
            } else {
                $this.menuButton.removeClass('active');
                $this.menuWrapper.removeClass('active');
            }
        });
        
        this.topbarMenu.clickOff(function(e) {
            if($this.topmenuButtonClick) {
                $this.topmenuButtonClick = false;
            } else {
                $this.topbarMenu.removeClass('active');
                $this.topmenuButton.removeClass('active');
            }
        });
        
    },
    
    removeMenuitem: function(id) {        
        this.expandedMenuitems = $.grep(this.expandedMenuitems, function(value) {
            return value !== id;
        });
    },
    
    addMenuitem: function(id) {
        if($.inArray(id, this.expandedMenuitems) === -1) {
            this.expandedMenuitems.push(id);
        }
    },
    
    saveMenuState: function() {
        $.cookie('Opencell_expandeditems', this.expandedMenuitems.join(','), {path:'/'});
    },
    
    clearMenuState: function() {
        $.removeCookie('Opencell_expandeditems', {path:'/'});
    },
    
    restoreMenuState: function() {
        var menucookie = $.cookie('Opencell_expandeditems');
        if(menucookie) {
            this.expandedMenuitems = menucookie.split(',');
            for(var i = 0; i < this.expandedMenuitems.length; i++) {
                var id = this.expandedMenuitems[i];
                if(id) {
                    var menuitem = $("#" + this.expandedMenuitems[i].replace(/:/g,"\\:"));
                    menuitem.addClass('active-menu-parent');
                    menuitem.children('a,ul').addClass('active-menu');
                }             
            }
        }
    },
    
    isMobile: function() {
        return (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(window.navigator.userAgent));
    }
};

$(function() {
   Opencell.init();
});