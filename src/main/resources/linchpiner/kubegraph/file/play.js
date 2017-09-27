$(document).ready(function play() {
	var n = 0;
	function step() {
		eval(_events[n][1]);
		if (n < _events.length - 1) {
			n++;
			setTimeout(step, _events[n][0]);
		}
	}
	setTimeout(step, _events[n][0]);
});
