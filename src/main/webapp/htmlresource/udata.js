(function(e, y) {
	var d = (("https:" == y.location.protocol) ? " https://" : " http://"), h = d
			+ "da.mmarket.com/udata/u.gif?", r = "300009974188", p = true, j = "udata_lt_"
			+ r, n = "1484306966718", s = undefined, m = "udata_s_" + r, k = "1.0", u = x().length;
	var w = {
		_st : new Date(),
		setStartTime : function(i) {
			this._st = i
		},
		getStartTime : function() {
			return this._st.getTime()
		},
		setAutoPageView : function(i) {
			p = i
		},
		setAccount : function(i) {
			s = i;
			if (c) {
				c.cookieHandle.setCookie("udata_account_" + r, i)
			}
		}
	};
	if (e._udata) {
		for (var t = 0; t < e._udata.length;) {
			if (w[e._udata[t][0]]) {
				w[e._udata[t][0]](e._udata[t][1]);
				e._udata.splice(t, t + 1)
			} else {
				++t
			}
		}
	}
	var v = new Date().getTime();
	var q = null;
	function l(z, i) {
		return (Array(i + 1).join("0") + (z || "")).slice(-i)
	}
	function x() {
		var i = ("" + Math.random()).substr(2, 6);
		return "" + new Date().getTime() + i
	}
	var f = {};
	f.EventHandler = {
		addEvent : (function(i) {
			if (i.addEventListener) {
				return function(B, A, z) {
					return B.addEventListener(A, z)
				}
			}
			if (i.attachEvent) {
				return function(B, A, z) {
					return B.attachEvent("on" + A, z)
				}
			}
		})(y)
	};
	var c = {};
	c.request = {
		setArgs : function(A) {
			var i = "", z;
			for (z in A) {
				if (A.hasOwnProperty(z)) {
					i += encodeURIComponent(z) + "=" + encodeURIComponent(A[z])
							+ "&"
				}
			}
			this.args = i.slice(0, -1)
		},
		send : function(z) {
			if (z) {
				this.setArgs(z)
			}
			var i = new Image();
			i.src = h + this.args;
			i.style.height = "0px";
			y.body.appendChild(i);
			i.onerror = function() {
				y.body.removeChild(i)
			};
			i.onload = function() {
				y.body.removeChild(i)
			}
		},
		sendEvent : function(i, z) {
			if (arguments.length < 2) {
				return
			}
			this.sendBase({
				et : i || "",
				lv : z || "",
				f : 3,
				cp : y.location.href
			})
		},
		sendBase : function(C) {
			var A = b.getSize(), D;
			var B = c.cookieHandle.getCookie(m);
			if (!B || B.toString().length != u) {
				if (e.navigator.cookieEnabled) {
					D = x();
					c.cookieHandle.setCookie(m, D)
				} else {
					return
				}
				B = c.cookieHandle.getCookie(m) || D;
				B = B.length == u ? B : D
			}
			var i = {
				h : A.height,
				w : A.width,
				ct : new Date().getTime(),
				si : r,
				cu : y.location.host,
				v : k,
				s : B,
				f : 1,
				c : n
			};
			if (s) {
				i.a = s
			}
			for ( var z in C) {
				if (C.hasOwnProperty(z)) {
					i[z] = C[z]
				}
			}
			this.send(i)
		}
	};
	c.cookieHandle = {
		getCookie : function(B) {
			B = encodeURIComponent(B);
			var A = y.cookie.split(";"), z = A.length, C = 0;
			for (C; C < z; ++C) {
				if (A[C] && (A[C].split("=")[0].replace(/\s/g, "") === B)) {
					return decodeURIComponent(A[C].split("=")[1])
				}
			}
			return null
		},
		setCookie : function(z, A, i) {
			if (i) {
				exp = "expires=" + i.toGMTString() + ";"
			} else {
				exp = ""
			}
			y.cookie = encodeURIComponent(z) + "=" + encodeURIComponent(A)
					+ ";path=/;" + exp
		}
	};
	if (s) {
		c.cookieHandle.setCookie("udata_account_" + r, s)
	} else {
		s = c.cookieHandle.getCookie("udata_account_" + r)
	}
	var b = {
		getSize : function() {
			var i = e.innerWidth, z = e.innerHeight;
			if (typeof i !== "number") {
				if (y.compatMode === "CSS1Compat") {
					i = y.documentElement.clientWidth;
					z = y.documentElement.clientHeight
				} else {
					i = y.body.clientWidth;
					z = y.body.clientHeight
				}
			}
			return {
				width : i,
				height : z
			}
		}
	};
	var g = {
		initApp : function() {
			var i = new Date();
			if (!c.cookieHandle.getCookie(m)) {
				c.cookieHandle.setCookie(m, x())
			}
			if (!c.cookieHandle.getCookie(j)) {
				var A = new Date(), z = b.getSize();
				A.setHours(24);
				A.setMinutes(0);
				A.setSeconds(0);
				c.cookieHandle.setCookie(j, x(), A);
				c.request.sendBase({
					f : 1
				})
			}
		},
		autoCollection : function(i) {
			if (!i) {
				i = {}
			}
			if (typeof i === "string") {
				var z = w.getStartTime();
				i = {
					cp : i,
					st : 2,
					t : new Date().getTime() - z
				}
			} else {
				i.st = 1
			}
			c.request.sendBase({
				cp : i.cp || y.location.href,
				fp : i.fp || y.referrer,
				t : i.t || q,
				st : i.st,
				f : 2
			})
		}
	};
	var a = e._udata;
	f.EventHandler.addEvent(y, "readystatechange", (function() {
		var i = false;
		return function() {
			if (!i) {
				i = true;
				if (e.navigator.cookieEnabled) {
					g.initApp()
				}
				e._udata = {};
				e._udata.sendEvent = function(z, A) {
					c.request.sendEvent(z, A)
				};
				e._udata.sendPageInfo = function(z) {
					if (!p) {
						g.autoCollection(z)
					}
					if (z === "sender") {
						g.autoCollection(this)
					}
				};
				e._udata.push = function(z) {
					if (z instanceof Array) {
						var A = z.splice(0, 1)
					}
					if (!this[A]) {
						return
					}
					this[A](z[0], z[1])
				}
			}
		}
	})());
	var o = function() {
		var i = new Date().getTime();
		var A = w.getStartTime();
		q = i - A;
		if (!e.navigator.cookieEnabled) {
			return
		}
		if (p) {
			g.autoCollection()
		}
		if (a) {
			var z = 0;
			for (; z < a.length; ++z) {
				e._udata.push(a[z])
			}
		}
	};
	f.EventHandler.addEvent(e, "load", o);
	if (document.readyState === "complete") {
		o()
	}
})(window, document);