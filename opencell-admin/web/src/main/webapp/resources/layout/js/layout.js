// FNC for detecting for click outside of any elements ============== 
$.fn.clickOff = function(callback, selfDestroy) {
	var clicked = false;
	var parent = this;
	var destroy = selfDestroy || true;

	parent.click(function() {
		clicked = true;
	});

	$(document).click(function(event) {
		if (!clicked) {
			callback(parent, event);
		}
		if (destroy) {
		}
		clicked = false;
	});
};

/**
 * PrimeFaces Opencell Layout
 */
var Opencell = {

	init : function() {
		this.menuWrapper = $('#layout-menu-cover');
		this.menu = this.menuWrapper.find('ul.layout-menu');
		this.menuLinks = this.menu.find('a.menulink');
		this.menuButton = $('#menu-button');
		this.topMenuButton = $('#topmenu-button');
		this.topBarMenu = $('#topbar-menu');
		this.expandedMenuitems = this.expandedMenuitems || [];

		this.bindEvents();
	},

	bindEvents : function() {
		var $this = this;

		this.menuButton.on('click', function() {
			$this.menuButtonClick = true;

			if ($this.menuWrapper.hasClass('active')) {
				$this.menuButton.removeClass('active');
				$this.menuWrapper.removeClass('active');
			} else {
				$this.menuButton.addClass('active');
				$this.menuWrapper.addClass('active');
				$this.topBarMenu.removeClass('active');
				$this.topMenuButton.removeClass('active');
			}

			// Equalizing of height of first level nodes of main menu
			$('.OpencellMegaMenu .layout-menu > li').equalHeights(true);
		});

		this.topMenuButton.on('click', function() {
			$this.topMenuButtonClick = true;

			if ($this.topBarMenu.hasClass('active')) {
				$this.topBarMenu.removeClass('active');
				$this.topMenuButton.removeClass('active');
			} else {
				$this.topBarMenu.addClass('active');
				$this.topMenuButton.addClass('active');
				$this.menuButton.removeClass('active');
				$this.menuWrapper.removeClass('active');
			}
		});

		this.menuLinks.on('click', function(e) {
			var menuitemLink = $(this), menuitem = menuitemLink.parent();

			if (menuitem.hasClass('active-menu-parent')) {
				menuitem.removeClass('active-menu-parent');
				menuitemLink.removeClass('active-menu').next('ul').removeClass(
						'active-menu');
				$this.removeMenuitem(menuitem.attr('id'));
			} else {
				var activeSibling = menuitem.siblings('.active-menu-parent');
				if (activeSibling.length) {
					activeSibling.removeClass('active-menu-parent');
					$this.removeMenuitem(activeSibling.attr('id'));

					activeSibling.find('ul.active-menu,a.active-menu')
							.removeClass('active-menu');
					activeSibling.find('li.active-menu-parent').each(
							function() {
								var menuitem = $(this);
								menuitem.removeClass('active-menu-parent');
								$this.removeMenuitem(menuitem.attr('id'));
							});
				}

				menuitem.addClass('active-menu-parent');
				menuitemLink.addClass('active-menu').next('ul').addClass(
						'active-menu');
				$this.addMenuitem(menuitem.attr('id'));
			}

			if (menuitemLink.next().is('ul')) {
				e.preventDefault();
			} else {
				$this.menuButton.removeClass('active');
				$this.menuWrapper.removeClass('active');
			}

			equalheight('.OpencellMegaMenu .layout-menu > li');

			$this.saveMenuState();
		});

		this.menuWrapper.clickOff(function(e) {
			if ($this.menuButtonClick) {
				$this.menuButtonClick = false;
			} else {
				$this.menuButton.removeClass('active');
				$this.menuWrapper.removeClass('active');
			}
		});

		this.topBarMenu.clickOff(function(e) {
			if ($this.topMenuButtonClick) {
				$this.topMenuButtonClick = false;
			} else {
				$this.topBarMenu.removeClass('active');
				$this.topMenuButton.removeClass('active');
			}
		});

	},

	removeMenuitem : function(id) {
		this.expandedMenuitems = $.grep(this.expandedMenuitems,
				function(value) {
					return value !== id;
				});
	},

	addMenuitem : function(id) {
		if ($.inArray(id, this.expandedMenuitems) === -1) {
			this.expandedMenuitems.push(id);
		}
	},

	saveMenuState : function() {
		$.cookie('Opencell_expandeditems', this.expandedMenuitems.join(','), {
			path : '/'
		});
	},

	clearMenuState : function() {
		$.removeCookie('Opencell_expandeditems', {
			path : '/'
		});
	},

	restoreMenuState : function() {
		var menucookie = $.cookie('Opencell_expandeditems');
		if (menucookie) {
			this.expandedMenuitems = menucookie.split(',');
			for (var i = 0; i < this.expandedMenuitems.length; i++) {
				var id = this.expandedMenuitems[i];
				if (id) {
					var menuitem = $("#"
							+ this.expandedMenuitems[i].replace(/:/g, "\\:"));
					menuitem.addClass('active-menu-parent');
					menuitem.children('a,ul').addClass('active-menu');
				}
			}
		}
	},

	isMobile : function() {
		return (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
				.test(window.navigator.userAgent));
	}
};

$(function() {
	Opencell.init();
});


if (typeof PrimeFaces.widget.Menubar != "undefined") {
	PrimeFaces.widget.Menubar.prototype.deactivate = function(b, a) {
	    var menu = this;
	    menu.activeitem = null;
	    b.children("a.ui-menuitem-link").removeClass(
	                 "ui-state-hover ui-state-active");
	    b.removeClass("ui-menuitem-active ui-menuitem-highlight");
	    if (a) {
	          b.children("ul.ui-menu-child").fadeOut("fast");
	    } else {
	          b.children("ul.ui-menu-child").hide();
	          if (!this.timer) {
	                 this.timer = setTimeout(function() {
	                        menu.reset();
	                 }, 300);
	          }
	    }
	}
	
	PrimeFaces.widget.Menubar.prototype.reactivate = function(d) {
	    if (this.timer) {
	          clearTimeout(this.timer);
	          this.timer = null;
	    }
	    this.activeitem = d;
	    var c = d.children("ul.ui-menu-child"), b = c
	                 .children("li.ui-menuitem-active:first"), a = this;
	    if (b.length == 1) {
	          a.deactivate(b)
	    }
	}
	
	PrimeFaces.widget.Menubar.prototype.activate = function(b) {
	    if (this.timer) {
	          clearTimeout(this.timer);
	          this.timer = null;
	    }
	    this.highlight(b);
	    var a = b.children("ul.ui-menu-child");
	    if (a.length == 1) {
	          this.showSubmenu(b, a)
	    }
	}
}