$(document).ready(function ( ) {
	var charts = new Object();
	var chartsOptions = new Object();
	var loadingPlayerGraphs = 0;
	var loadingTeamGraphs = 0;
	var selectedPlayer = '';
	var selectedTeam = '';
	var batterCharts = ['chart_ba', 'chart_ba_moving', 'chart_ba_volatility', 'chart_batter_style', 'chart_slg',
					    'chart_obp', 'chart_slg_moving', 'chart_obp_moving', 'chart_slg_volatility',
					    'chart_obp_volatility', 'chart_fanduel_fantasy', 'chart_fanduel_fantasy_moving',
					    'chart_draftkings_fantasy', 'chart_draftkings_fantasy_moving', 'chart_draftster_fantasy', 'chart_draftster_fantasy_moving'];
	var pitcherCharts = ['chart_pitcher_outs', 'chart_pitcher_style', 'chart_pitcher_strikeratio', 'chart_fanduel_fantasy', 'chart_fanduel_fantasy_moving',
					    'chart_draftkings_fantasy', 'chart_draftkings_fantasy_moving', 'chart_draftster_fantasy', 'chart_draftster_fantasy_moving'];
	var teamCharts = ['chart_team_fantasy', 'chart_ballpark_ba', 'chart_ballpark_conditions', 'chart_ballpark_attendance'];
  	$.ajax({
		url: '/years',
		cache: false
	}).done (function (years) {
		$('#yearList').empty();
		$.each(years, function(key, val) {
		    $('#yearList').append('<li id=\"' + val + '\"><a href=\"#\">' + val + '</a></li>');
		});
		$('ul#yearList li').on('click', handleYearSelect);
	});
	function handleYearSelect() {
		$('#selectedYearText').text(this.innerText);
		if (selectedPlayer != '') {
			$('#' + selectedPlayer).trigger('click');
		} else if (selectedTeam != '') {
			$('#' + selectedTeam).trigger('click');
		}
		$('#playerSummary').html('');
		$.ajax({
			url: '/teams?year=' + $('#selectedYearText').text(),
			cache: false
		}).done (function (teams) {
			$('#teamList').empty();
			$.each(teams, function(key, val) {
				$('#teamList').append('<li id=\"' + val.mnemonic + '\"><a href=\"#\">' + val.city + ' ' + val.name + ' (' + val.league + ')</a></li>');
			});
			$('ul#teamList li').on('click', handleTeamSelect);
		});
	}
	function handleTeamSelect() {
		$('#selectedTeamText').text(this.innerText);
		$('#selectedPlayerText').text('Players');
		$('#teamSelectStatus').removeClass('hide');
		$('#schedule').removeClass('hide');
		$('#injuries').removeClass('hide');
		$('#playerSummary').html('');
		selectedTeam = this.id;
		batterCharts.map(hideChart);
		pitcherCharts.map(hideChart);
		teamCharts.map(showChart);
		loadingPlayerGraphs = 0;
		var parameters = 'team=' + this.id + '&year=' + $('#selectedYearText').text();
		$.ajax({
			url: '/players?' + parameters,
			cache: false
		}).done (function (players) {
			$('ul#playerList').empty();
			var pitcherStarted = false;
			$.each(players, function(key, player) {
				if (player.position == 'P' && !pitcherStarted) {
					pitcherStarted = true;
					$('#playerList').append('<li role=\"presentation\" class=\"divider\"></li>');
				}
				$('#playerList').append('<li id=\"' + player.id + '\"><a href=\"#\">' + player.firstName + ' ' + player.lastName + ' (' + player.position + ')</a></li>');
			});
			$('ul#playerList li').on('click', handlePlayerSelect);
		});
		$.ajax({
			url: '/team/schedule?' + parameters,
			cache: false
		}).done (function (schedule) {
			$('tbody#schedule_table_body').empty();
			$.each(schedule, function(key, fullGameInfo) {
				var temp = '';
				var game = fullGameInfo.schedule;
				var odds = fullGameInfo.odds;
				if (game.temp != '0') temp = game.temp;
				var record = '';
				var homeML;
				var visitorML;
				if (game.homeTeam == selectedTeam) {
					record = game.record;
					homeML = odds.homeML;
					visitorML = odds.visitorML;
				} else {
					homeML = odds.visitorML;
					visitorML = odds.homeML;
				}
				$('#schedule_table_body').append('<tr>' + 
					'<td>' + game.date + '</td>' +
					'<td>' + game.visitingTeam + '</td>' +
					'<td>' + game.homeTeam + '</td>' +
					'<td>' + game.result + '</td>' +
					'<td>' + record + '</td>' +
					'<td>' + game.winningPitcher + '</td>' +
					'<td>' + game.losingPitcher + '</td>' +
					'<td align=\'right\'>' + visitorML + '</td>' +
					'<td align=\'right\'>' + homeML + '</td>' +
					'<td>' + game.startTime + '</td>' +
					'<td>' + temp + '</td>' +
					'<td>' + game.sky + '</td>' +
					'</tr>'
					);
			});
		});
		$.ajax({
			url: '/team/injuries?team=' + selectedTeam,
			cache: false
		}).done (function (schedule) {
			$('tbody#injuries_table_body').empty();
			$.each(schedule, function(key, injury) {
				$('#injuryReportTime').html("&nbsp(as of: " + injury.reportTime + ")");
				$('#injuries_table_body').append('<tr>' + 
					'<td>' + injury.injuryReportDate + '</td>' +
					'<td>' + injury.mlbId + '</td>' +
					'<td>' + injury.status + '</td>' +
					'<td>' + injury.dueBack + '</td>' +
					'<td>' + injury.injury + '</td>' +
					'</tr>'
				);
			});
		});
		loadingTeamGraphs = 4;
		getDataAndDrawChart('/team/fantasy?' + parameters, 'Total Fantasy Score', 'chart_team_fantasy', '');
		getDataAndDrawChart('/team/ballparkBA?' + parameters, '25 Day Ballpark Batting Average', 'chart_ballpark_ba', '');
		getDataAndDrawChart('/team/ballparkConditions?' + parameters, 'Ballpark Conditions Forecast', 'chart_ballpark_conditions', '');
		getDataAndDrawChart('/team/ballparkAttendance?' + parameters, 'Ballpark Attendance', 'chart_ballpark_attendance', '');
	};
	function getDataAndDrawChart(url, title, chartName) {
		$.ajax({
		  url: url,
		  dataType: 'json',
		  success: function (data, textStatus, xhr) {
			// Create our data table out of JSON data loaded from server.
			var dataTable = new google.visualization.DataTable(data);
			if (title in charts) {
				var chart = charts[title];
				chart.draw(dataTable, chartsOptions[title]);      
			} else {
				var chart 
				if (title == 'Outs') {
					var options = {
						title: title,
						isStacked: true,
						legend: { position: 'bottom' },
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.SteppedAreaChart(document.getElementById(chartName));
				} else if (title == 'Outs Types') {
					var options = {
						title: title,
						isStacked: true,
						legend: 'none',
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.PieChart(document.getElementById(chartName));
				} else if (title == 'Style') {
					var options = {
						title: title,
						isStacked: true,
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.PieChart(document.getElementById(chartName));
				} else if (title == 'Ballpark Attendance') {
					var options = {
						title: title,
						vAxis: {maxValue: 50000, minValue: 0, gridlines: {count: 6}},
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				} else if (title == '25 Day Ballpark Batting Average') {
					var options = {
						title: title,
						vAxis: {maxValue: 0.5, minValue: 0, gridlines: {count: 6}},
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				} else if (title == 'Ballpark Conditions Forecast') {
					var options = {
						title: title,
						vAxis: {maxValue: 100, minValue: 0},
						animation: {
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				} else if (title == 'Total Fantasy Score') {
					var options = {
						title: title,
						vAxis: {maxValue: 100, minValue: 0},
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				} else if (title.indexOf("Volatility") > -1) {
					var options = {
						title: title,
						vAxis: {maxValue: 0.3, minValue: 0, gridlines: {count: 5}},
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				} else if (title.indexOf("Moving average") > -1) {
					if ((title.indexOf("FanDuel") > -1) || (title.indexOf("Draftster") > -1)) {
						var options = {
							title: title,
							vAxis: {maxValue: 10, minValue: 0, gridlines: {count: 6}},
							animation:{
								duration: 1000,
								easing: 'out'
							}
						};
					} else {
						var options = {
							title: title,
							vAxis: {maxValue: 20, minValue: 0, gridlines: {count: 5}},
							animation:{
								duration: 1000,
								easing: 'out'
							}
						};
					}
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				} else {
					var options = {
						title: title,
						animation:{
							duration: 1000,
							easing: 'out'
						}
					};
					var chart = new google.visualization.LineChart(document.getElementById(chartName));
				}
				charts[title] = chart
				chartsOptions[title] = options
				chart.draw(dataTable, options);      
			}
			loadingPlayerGraphs--;
			if (loadingPlayerGraphs <= 0) $('#playerSelectStatus').addClass('hide');		
			loadingTeamGraphs--;
			if (loadingTeamGraphs <= 0) $('#teamSelectStatus').addClass('hide');		
		  },
		  error: function (xhr, textStatus, errorThrown) {
			console.log(textStatus);
		  }
		});
	};
	function drawPlayerChart(player, endpoint, title, chartName, gameName) {
		loadingPlayerGraphs++;
		var isBatter = $('#selectedPlayerText').text().indexOf('(P)') == -1;
		if (loadingPlayerGraphs == 1) $('#playerSelectStatus').removeClass('hide');		
		var summaryUrl = '/batter/summary?player=' + player + '&year=' + $('#selectedYearText').text();
		if (!isBatter) {
			summaryUrl = '/pitcher/summary?player=' + player + '&year=' + $('#selectedYearText').text();
		}
		$.ajax({
		  url: summaryUrl,
		  dataType: 'json',
		  cache: false,
		  success: function (data, textStatus, xhr) {
		  	var externalLinks = '';
		  	if (data.appearances.mlbId.length > 0) externalLinks = externalLinks.concat('<a href=\'http://mlb.mlb.com/team/player.jsp?player_id=' + data.appearances.mlbId + '\' target=\'_blank\'><strong>&nbsp;&nbsp;&nbsp;mlb.com</strong></a>');
		  	if (data.appearances.espnId.length > 0) externalLinks = externalLinks.concat('<a href=\'http://espn.go.com/mlb/player/_/id/' + data.appearances.espnId + '\' target=\'_blank\'><strong>&nbsp;&nbsp;&nbsp;espn.com</strong></a>');
		  	if (data.appearances.brefId.length > 0) externalLinks = externalLinks.concat('<a href=\'http://www.baseball-reference.com/players/' + data.appearances.brefId[0] + '/' + data.appearances.brefId + '.shtml\' target=\'_blank\'><strong>&nbsp;&nbsp;bref.com</strong></a>');
		  	if (isBatter) {
		  		var lineupRegime = data.appearances.lineupRegime;
		  		if (lineupRegime == 0) lineupRegime = '(UNK)'
			  	$('#playerSummary').html('<b>Bats:</b>&nbsp' + data.meta.batsWith + ',&nbsp<b>Throws:</b>&nbsp' + data.meta.throwsWith + 
			  		',&nbsp<b>RH At Bats:</b>&nbsp' + data.appearances.RHatBats + ',&nbsp<b>LH At Bats:</b>&nbsp' + data.appearances.LHatBats + 
			  		',&nbsp<b>Games:</b>&nbsp' + data.appearances.games + ',&nbsp<b>Lineup Regime:</b>&nbsp' + lineupRegime +
			  		externalLinks);
			} else {
			  	$('#playerSummary').html('<b>Bats:</b>&nbsp' + data.meta.batsWith + ',&nbsp<b>Throws:</b>&nbsp' + data.meta.throwsWith + 
			  		',&nbsp<b>Wins:</b>&nbsp' + data.appearances.wins + ',&nbsp<b>Losses:</b>&nbsp' + data.appearances.losses + 
			  		',&nbsp<b>Saves:</b>&nbsp' + data.appearances.saves + ',&nbsp<b>Avg Days Between:</b>&nbsp' + data.appearances.daysSinceLastApp +
			  		externalLinks);
			}
		  },
		  error: function (xhr, textStatus, errorThrown) {
		  	console.log(textStatus);	
		  }
		});
		var url = endpoint + '?player=' + player + '&year=' + $('#selectedYearText').text();
		if (gameName != '') {
			url = endpoint + '?player=' + player + '&gameName=' + gameName + '&year=' + $('#selectedYearText').text();
		}
		getDataAndDrawChart(url, title, chartName);
	}
	function hideChart(chartname) {
		$('#' + chartname).addClass('hide');
	}
	function showChart(chartname) {
		$('#' + chartname).removeClass('hide');
	}
	function handlePlayerSelect() {
		$('#selectedPlayerText').text(this.innerText);
		$('#schedule').addClass('hide');
		$('#injuries').addClass('hide');
		selectedPlayer = this.id;
		if ($('#selectedPlayerText').text().indexOf('(P)') == -1) {
		  $('#playerSelectStatus').removeClass('hide');
		  pitcherCharts.map(hideChart);
		  teamCharts.map(hideChart);
		  batterCharts.map(showChart);
		  drawPlayerChart(this.id, '/batter/BA', 'As of Date Batting Average', 'chart_ba', '');
		  drawPlayerChart(this.id, '/batter/movingBA', '25 Day Batting Averages', 'chart_ba_moving', '');
		  drawPlayerChart(this.id, '/batter/volatilityBA', '50 Day Batting Average Volatility', 'chart_ba_volatility', '');
		  drawPlayerChart(this.id, '/batter/style', 'Style', 'chart_batter_style', '');
		  drawPlayerChart(this.id, '/batter/slugging', 'Slugging percentage', 'chart_slg', '');
		  drawPlayerChart(this.id, '/batter/onBase', 'On base percentage', 'chart_obp', '');
		  drawPlayerChart(this.id, '/batter/sluggingMoving', '25 Day Slugging percentage', 'chart_slg_moving', '');
		  drawPlayerChart(this.id, '/batter/onBaseMoving', '25 Day On base percentage', 'chart_obp_moving', '');
		  drawPlayerChart(this.id, '/batter/sluggingVolatility', '50 Day Slugging Volatility', 'chart_slg_volatility', '');
		  drawPlayerChart(this.id, '/batter/onBaseVolatility', '50 Day On base Volatility', 'chart_obp_volatility', '');
		  drawPlayerChart(this.id, '/batter/fantasy', 'Daily FanDuel score', 'chart_fanduel_fantasy', 'FanDuel');
		  drawPlayerChart(this.id, '/batter/fantasyMoving', 'Moving average FanDuel score', 'chart_fanduel_fantasy_moving', 'FanDuel');
		  drawPlayerChart(this.id, '/batter/fantasy', 'Daily DraftKings score', 'chart_draftkings_fantasy', 'DraftKings');
		  drawPlayerChart(this.id, '/batter/fantasyMoving', 'Moving average DraftKings score', 'chart_draftkings_fantasy_moving', 'DraftKings');
		  drawPlayerChart(this.id, '/batter/fantasy', 'Daily Draftster score', 'chart_draftster_fantasy', 'Draftster');
		  drawPlayerChart(this.id, '/batter/fantasyMoving', 'Moving average Draftster score', 'chart_draftster_fantasy_moving', 'Draftster');
		} else {
		  $('#playerSelectStatus').removeClass('hide');
		  batterCharts.map(hideChart);
		  teamCharts.map(hideChart);
		  pitcherCharts.map(showChart);
		  drawPlayerChart(this.id, '/pitcher/outs', 'Outs', 'chart_pitcher_outs', '');
		  drawPlayerChart(this.id, '/pitcher/outsTypes', 'Outs Types', 'chart_pitcher_style', '');
		  drawPlayerChart(this.id, '/pitcher/strikeRatio', 'Strike Ratio', 'chart_pitcher_strikeratio', '');
		  drawPlayerChart(this.id, '/pitcher/fantasy', 'Daily FanDuel score', 'chart_fanduel_fantasy', 'FanDuel');
		  drawPlayerChart(this.id, '/pitcher/fantasyMoving', 'Moving average FanDuel score', 'chart_fanduel_fantasy_moving', 'FanDuel');
		  drawPlayerChart(this.id, '/pitcher/fantasy', 'Daily DraftKings score', 'chart_draftkings_fantasy', 'DraftKings');
		  drawPlayerChart(this.id, '/pitcher/fantasyMoving', 'Moving average DraftKings score', 'chart_draftkings_fantasy_moving', 'DraftKings');
		  drawPlayerChart(this.id, '/pitcher/fantasy', 'Daily Draftster score', 'chart_draftster_fantasy', 'Draftster');
		  drawPlayerChart(this.id, '/pitcher/fantasyMoving', 'Moving average Draftster score', 'chart_draftster_fantasy_moving', 'Draftster');
		}
	};
});
