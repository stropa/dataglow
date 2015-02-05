

function JmxChartsFactory(keepHistorySec, pollInterval, columnsCount, url) {
	var jolokia = new Jolokia(url);
	var series = [];
	var monitoredMbeans = [];
	var chartsCount = 0;

	columnsCount = columnsCount || 3;
	pollInterval = pollInterval || 1000;
	var keepPoints = (keepHistorySec || 600) / (pollInterval / 1000);

	setupPortletsContainer(columnsCount);

	setInterval(function() {
		pollAndUpdateCharts();
	}, pollInterval);

	this.create = function(mbeans, chartName) {
		mbeans = $.makeArray(mbeans);
		series = series.concat(this.createChart(mbeans, chartName).series);
		monitoredMbeans = monitoredMbeans.concat(mbeans);
	};

	function pollAndUpdateCharts() {
		var requests = prepareBatchRequest();
		var responses = jolokia.request(requests);
		updateCharts(responses);
	}

	function createNewPortlet(name) {
		return $('#portlet-template')
				.clone(true)
				.appendTo($('.column')[chartsCount++ % columnsCount])
				.removeAttr('id')
				.find('.title').text((name.length > 50? '...' : '') + name.substring(name.length - 50, name.length)).end()
				.find('.portlet-content')[0];
	}

	function setupPortletsContainer() {
		var column = $('.column');
		for(var i = 1; i < columnsCount; ++i){
			column.clone().appendTo(column.parent());
		}
		$(".column").sortable({
			connectWith: ".column"
		});

		$(".portlet-header .ui-icon").click(function() {
			$(this).toggleClass("ui-icon-minusthick").toggleClass("ui-icon-plusthick");
			$(this).parents(".portlet:first").find(".portlet-content").toggle();
		});
		$(".column").disableSelection();
	}

	function prepareBatchRequest() {
		return $.map(monitoredMbeans, function(mbean) {
			return {
				type: "read",
				mbean: mbean.name,
				attribute: mbean.attribute,
				path: mbean.path
			};
		});
	}

	function updateCharts(responses) {
		var curChart = 0;
		$.each(responses, function() {
            var point = {
                x: this.timestamp * 1000,
                y: parseFloat(this.value)
            };
			var curSeries = series[curChart++];
			curSeries.addPoint(point, true, curSeries.data.length >= keepPoints);
		});
	}


}

function createChartForArtifact(artifact) {
	console.log(artifact.name + " WHOA!");

	var chart = createChart([
		{
			name: 'ru.bpc.payment.metrics.rest:type=SuccessCounter,name=registerSuccessCounter',
			attribute: 'OneMinuteRate',
			seriesName: 'registerSuccess'
		}
	], "REST API Success Counters", $('.render-chart-here')[0]);

	$.ajax({
		type: 'GET',
		url: 'mvc/series/forArtifact/' + artifact.id,
		//dataType: 'json',
		success: function (data) {
			debugger;
			//var parsedData = JSON.parse(data);
			$.each(data, function() {
				debugger;
				var point = {
					x: this.timestamp * 1000,
					y: this.value
				};
				debugger;
				chart.series[0].addPoint(point);
			})
		}
	})

}

createChart = function(mbeans, chartName, renderTo) {
	debugger;
	return new Highcharts.Chart({
		chart: {
			renderTo:  renderTo, //createNewPortlet(chartName == null ? mbeans[0].name : chartName),
			animation: false,
			defaultSeriesType: 'area',
			shadow: false
		},
		title: { text: null },
		xAxis: { type: 'datetime' },
		yAxis: {
			title: { text: mbeans[0].attribute }
		},
		legend: {
			enabled: true,
			borderWidth: 0
		},
		credits: {enabled: false},
		exporting: { enabled: false },
		plotOptions: {
			area: {
				marker: {
					enabled: false
				}
			}
		},
		series: $.map(mbeans, function(mbean) {
			return {
				data: [],
				name: mbean.seriesName != null ? mbean.seriesName : mbean.path || mbean.attribute
			}
		})
	})
}