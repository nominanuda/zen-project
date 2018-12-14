function boot(modules, retries, timeout, timer, log) {
	log && alert('boot');
	
	timer = timer || retries && setTimeout(function() {
		clearTimeout(timer);
		requirejs.undef(modules);
		log && alert('reboot');
		boot(modules, retries-1, timeout, null, log);
	}, 10000);
	
	require(['jquery'].concat(modules), function($) {
		clearTimeout(timer);
		log && alert('booted');
		var MODULES = arguments;
		$(function() {
			for (var i = 1; i < MODULES.length; i++) {
				var module = MODULES[i];
				module && typeof module == 'function' && module();
			}
		});
		
	}, function(err) {
		log && alert('error: ' + err.requireType + ' - ' + JSON.stringify(err.requireModules));
		if (err.requireType === 'timeout' && retries) {
			clearTimeout(timer);
			setTimeout(function() {
				boot(modules, retries-1, timeout, null, log);
			}, timeout || 1000);
		}
	});
}

function bootlog(modules, retries, timeout, timer) {
	boot(modules, retries, timeout, timer, true);
}