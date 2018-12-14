const IMAGES_URL_RE = /(http.+\/[^\.]+)(\.((\d*)x(\d*)\.)?([a-z]+))?/;


function img2obj(url) {
	var parts = IMAGES_URL_RE.exec(url);
	return parts ? {
		resource: parts[1],
		width: parseInt(parts[4]) || null,
		height: parseInt(parts[5]) || null,
		format: parts[6]
	} : null;
}

function obj2url(obj, width, height) {
	if (obj && obj.resource) {
		var url = obj.resource + '.';
		var w = (width !== undefined ? width : obj.width); // width == null -> img.xH.ext
		var h = (height !== undefined ? height : obj.height); // height == null -> img.Wx.ext 
		(w || h) && (url += (w || '') + 'x' + (h || '') + '.');
		return url + obj.format;
	}
	return null;
}

function render(obj, width, height, format) {
	obj = (typeof obj == 'string' ? img2obj(obj) : obj);
	obj && format && (obj.format = format);
	return obj2url(obj, width, height);
}


exports = {
	render: render,
	img2obj: img2obj,
	obj2url: obj2url
};