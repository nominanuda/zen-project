/*
 * Copyright 2008-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


define('zen-webapp/_alert_/boxes', [
        'jquery',
        'zen-webapp/overlay'
        ], function($, OVERLAY) {
	
	
	var ALERT_SOY = com.nominanuda.webapp.alert;
	var MSG_SELECTOR = 'p.msg';
	
	
	
	/* msgAlert */
	
	var $msgAlert = $(ALERT_SOY.msg({}));
	OVERLAY.box.cancel($msgAlert, function() {
		msgAlertCback && msgAlertCback();
	});
	
	var msgAlert = {
		activate: function(msg, $src, cback) {
			msgAlertCback = cback;
			$(MSG_SELECTOR, $msgAlert).text(msg);
			OVERLAY.box.on($src, $msgAlert, null, cback);
		}
	}, msgAlertCback;
	
	
	
	
	/* yesnoAlert */
	
	var $yesnoAlert = $(ALERT_SOY.yesno({}));
	$('.ctrls button', $yesnoAlert).on('click tap', function() {
		OVERLAY.box.off(function() {
			yesnoAlertCback && yesnoAlertCback(true);
		});
	});
	OVERLAY.box.cancel($yesnoAlert, function() {
		yesnoAlertCback && yesnoAlertCback(false);
	});
	
	var yesnoAlert = {
		activate: function(msg, $src, cback) {
			yesnoAlertCback = cback;
			$(MSG_SELECTOR, $yesnoAlert).text(msg);
			OVERLAY.box.on($src, $yesnoAlert, null, cback);
		}
	}, yesnoAlertCback;

	
	
	
	/* promptAlert */
	
	var $promptAlert = $(ALERT_SOY.prompt({}));
	var $promptAlertInput = $('.text', $promptAlert);
	$('form', $promptAlert).on('submit', function() {
		OVERLAY.box.off(function() {
			promptAlertCback && promptAlertCback($promptAlertInput.val());
		});
		return false;
	});
	OVERLAY.box.cancel($promptAlert, function() {
		promptAlertCback && promptAlertCback();
	});
	
	var promptAlert = {
		activate: function(msg, val, $src, cback) {
			promptAlertCback = cback;
			$promptAlertInput.val(val);
			$(MSG_SELECTOR, $promptAlert).text(msg);
			OVERLAY.box.on($src, $promptAlert, function() {
				$promptAlertInput.focus();
			}, cback);
		}
	}, promptAlertCback;
	

	
	
	return {
		message: msgAlert.activate,
		yesno: yesnoAlert.activate,
		prompt: promptAlert.activate
	};
});