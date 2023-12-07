let loadedSchedule = null;

var autoRefreshIntervalId = null;
var formattingOptions = 
{
    minorLabels: {
        millisecond:'',
        second:     '',
        minute:     'HH:mm',
        hour:       'HH:mm',
        weekday:    '',
        day:        'D',
        week:       '',
        month:      'MM',
        year:       ''        
    },
    majorLabels: {
        millisecond:'',
        second:     '',
        minute:     '',
        hour:       'dd D-MM',
        weekday:    'MMMM YYYY',
        day:        'MMMM YYYY',
        week:       'MMMM YYYY',
        month:      'YYYY',
        year:       ''        
    }
  };

const byCrewPanel = document.getElementById("byCrewPanel");
const byCrewTimelineOptions = {
    timeAxis: {scale: "hour", step: 6},
    orientation: {axis: "top"},
    stack: false,
    editable: {
        add: true,         // add new items by double tapping
        updateTime: true,  // drag items horizontally
        updateGroup: true, // drag items from one group to another
        remove: true,       // delete an item by tapping the delete button top right
        overrideItems: false  // allow these options to override item.editable
      },    
    multiselect: true,
    xss: {disabled: true}, // Items are XSS safe through JQuery
    zoomMin: 1000 * 60 * 60, // One hour in milliseconds
    locale: 'nl',
    format: formattingOptions,

    onRemove: function (item, callback) {
        Swal.fire({
            title: "Are you sure?",
            text: "You won't be able to revert this!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then( function(result) {
            /* Read more about isConfirmed, isDenied below */
            if (result.isConfirmed) {
                callback(item);
                removeJob(item);
            }
          });
      },

      onMove: function (item, callback) {
        Swal.fire({
            title: 'Item verplaatsen',
            html: `
            Weet je zeker dat je dit item wilt verplaatsen naar<br>
            Start: ` + item.start + `,<br>
            Eind: ` + item.end,
            showDenyButton: true,
            showCancelButton: true,
            confirmButtonText: "Save",
            denyButtonText: `Don't save`
        }).then( function(result) {
            /* Read more about isConfirmed, isDenied below */
            if (result.isConfirmed) {
                callback(item);
                updateJob(item);
            } else if (result.isDenied) {
                callback(null);
                Swal.fire("Wijziging niet opgeslagen", "", "info");
            }
          });
      }

};

var byCrewGroupDataSet = new vis.DataSet();
var byCrewItemDataSet = new vis.DataSet();
var byCrewTimeline = new vis.Timeline(byCrewPanel, byCrewItemDataSet, byCrewGroupDataSet, byCrewTimelineOptions);

const byJobPanel = document.getElementById("byJobPanel");
const byJobTimelineOptions = {
    timeAxis: {scale: "hour", step: 6},
    orientation: {axis: "top"},
    xss: {disabled: true}, // Items are XSS safe through JQuery
    zoomMin: 1000 * 60 * 60, // One hour in milliseconds
    locale: 'nl',
    format: formattingOptions
};

var byJobGroupDataSet = new vis.DataSet();
var byJobItemDataSet = new vis.DataSet();
var byJobTimeline = new vis.Timeline(byJobPanel, byJobItemDataSet, byJobGroupDataSet, byJobTimelineOptions);

const byCapacityPanel = document.getElementById("byCapacityPanel");
const byCapacityTimelineOptions = {
    timeAxis: {scale: "day", step: 1},
    orientation: {axis: "top"},
    xss: {disabled: true}, // Items are XSS safe through JQuery
    zoomMin: 1000 * 60 * 60 * 24, // One day in milliseconds
    locale: 'nl',
    format: formattingOptions
};

var byCapacityGroupDataSet = new vis.DataSet();
var byCapacityItemDataSet = new vis.DataSet();
var byCapacityTimeline = new vis.Timeline(byCapacityPanel, byCapacityItemDataSet, byCapacityGroupDataSet, byCapacityTimelineOptions);

$(document).ready(function () {
    replaceTimefoldAutoHeaderFooter();
    setupAjax();

    $("#refreshButton").click(function () {
        refreshSchedule();
    });
    $("#solveButton").click(function () {
        solve();
    });
    $("#stopSolvingButton").click(function () {
        stopSolving();
    });
    $("#analyzeButton").click(function () {
        analyze();
    });
    // HACK to allow vis-timeline to work within Bootstrap tabs
    $("#byCrewTab").on('shown.bs.tab', function (event) {
        byCrewTimeline.redraw();
    })
    $("#byJobTab").on('shown.bs.tab', function (event) {
        byJobTimeline.redraw();
    })
    $("#byCapacityTab").on('shown.bs.tab', function (event) {
        byCapacityTimeline.redraw();
    })

    refreshSchedule();
});

function refreshSchedule() {
    $.getJSON("/schedule", function (schedule) {
        loadedSchedule = schedule;

        refreshSolvingButtons(schedule.solverStatus != null && schedule.solverStatus !== "NOT_SOLVING");
        $("#score").text("Score: " + (schedule.score == null ? "?" : schedule.score));

        const unassignedJobs = $("#unassignedJobs");
        unassignedJobs.children().remove();
        var unassignedJobsCount = 0;
        byCrewGroupDataSet.clear();
        byJobGroupDataSet.clear();
        byCapacityGroupDataSet.clear();
        byCrewItemDataSet.clear();
        byJobItemDataSet.clear();
        byCapacityItemDataSet.clear();

        // Map Monteur ID's to Crew ID's for later usage in availabilty background
        var MonteurToCrew = new Map();

        $.each(schedule.crewList, (index, crew) => {
                const crewDescription = $(`<div/>`)
                .append(crew.name)
                $.each(crew.monteurs, (index, monteur) => {
                    MonteurToCrew.set(monteur.id, crew.id);

                    const capacityDescription = $(`<div/>`)
                    crewDescription.append(`</br>`)
                    crewDescription.append(monteur.naam)
                    crewDescription.append(` `)
                    crewDescription.append(monteur.vaardigheid.omschrijving)

                    capacityDescription.append(monteur.naam)
                    capacityDescription.append(`</br>`)
                    capacityDescription.append(monteur.vaardigheid.omschrijving)
                    byCapacityGroupDataSet.add({id : monteur.id, content: capacityDescription.html()});
                });
                byCrewGroupDataSet.add({id : crew.id, content: crewDescription.html()
            });
        });

        $.each(schedule.jobList, (index, job) => {
            const jobGroupElement = $(`<div/>`)
              .append($(`<h5 class="card-title mb-1"/>`).text(job.adres))
              .append($(`<p class="card-text ms-2 mb-0"/>`).text("Bestekcode: " + job.bestekcode))
              .append($(`<p class="card-text ms-2 mb-0"/>`).text("Verwachte uitvoeringsduur: " + `${job.durationInHours} uur`));
              $.each(job.requiredSkills, (index, req) => {
                jobGroupElement.append(`</br>`)
                jobGroupElement.append(req.aantal)
                jobGroupElement.append(` `)
                jobGroupElement.append(req.omschrijving)
            });
            byJobGroupDataSet.add({
                id : job.id,
                content: jobGroupElement.html()
            });

            if (job.crew == null || job.startDate == null) {
                unassignedJobsCount++;
                const unassignedJobElement = $(`<div class="card-body p-2"/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(job.adres))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`Bestekcode: ${job.bestekcode}`))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`Verwachte uitvoeringsduur: ${job.durationInHours} uur`));
                const byJobJobElement = $(`<div/>`)
                  .append($(`<h5 class="card-title mb-1"/>`).text(`Unassigned`));
                $.each(job.requiredSkills, (index, tag) => {
                    if (tag.omschrijving.toString().startsWith("VIAG"))
                    {
                        color = "#FEB900";
                    }
                    else if (tag.omschrijving.startsWith("BEI"))
                    {
                        color = "#ED5353";
                    }
                    else
                    {
                        color = "#003366";
                    }
                    unassignedJobElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                    byJobJobElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                });
                unassignedJobs.append($(`<div class="col"/>`).append($(`<div class="card"/>`).append(unassignedJobElement)));
            } else {
                const byCrewJobElement = $(`<div/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(job.adres))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`${job.bestekcode}: ${job.durationInHours} uur`));
                const byJobJobElement = $(`<div/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(job.crew.name));
                $.each(job.requiredSkills, (index, tag) => {
                    if (tag.omschrijving.toString().startsWith("VIAG")) {color = "#FEB900";}
                    else if (tag.omschrijving.toString().startsWith("BEI")) {color = "#ED5353";}
                    else {color = "#003366";}
                    byCrewJobElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                    byJobJobElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                });
                byCrewItemDataSet.add({
                    id : job.id, group: job.crew.id,
                    content: byCrewJobElement.html(),
                    start: job.startDate, end: job.endDate
                });
                byJobItemDataSet.add({
                    id : job.id, group: job.id,
                    content: byJobJobElement.html(),
                    start: job.startDate, end: job.endDate
                });
            }
        });

        var MonteurInCrewCount = {};
        MonteurToCrew.forEach(function (x) {
            MonteurInCrewCount[x] = (MonteurInCrewCount[x] || 0) + 1
        });

        var CrewMemberCount = 1;
        var PreviousMonteurID;

        $.each(schedule.beschikbaarheidList, (index, beschikbaarheid) => {
            if (beschikbaarheid.monteur.id == PreviousMonteurID) {}
            else if (MonteurToCrew.get(beschikbaarheid.monteur.id) == MonteurToCrew.get(beschikbaarheid.monteur.id - 1)) {CrewMemberCount++;}
            else {CrewMemberCount = 1;}

            PreviousMonteurID = beschikbaarheid.monteur.id;

            const byCapacityElement = $(`<div/>`)
                .append($(`<h5 class="card-title mb-1"/>`).text(beschikbaarheid.beschikbaarheidType.toString()));
            byCapacityItemDataSet.add({
                id : beschikbaarheid.id, group: beschikbaarheid.monteur.id,
                content: byCapacityElement.html(),
                start: beschikbaarheid.date, end: JSJoda.LocalDate.parse(beschikbaarheid.date).plusDays(1).toString()
            });

            // Add background color to Crew planning if there's an unavailable or sick employee
            if (MonteurToCrew.has(beschikbaarheid.monteur.id)) {
                if (beschikbaarheid.beschikbaarheidType.toString() == "ONBESCHIKBAAR" || beschikbaarheid.beschikbaarheidType.toString() == "ZIEK") {
                    var nameElement = $(`<div/>`);
                    nameElement.append("Onbeschikbaar:</br>");
                    if (CrewMemberCount > 1) {
                        for (let index = 1; index < CrewMemberCount; index++) {
                            nameElement.append("</br>");
                        }
                    }
                    nameElement.append(beschikbaarheid.monteur.naam);
                    byCrewItemDataSet.add({
                        group: MonteurToCrew.get(beschikbaarheid.monteur.id),
                        start: beschikbaarheid.date, end: JSJoda.LocalDate.parse(beschikbaarheid.date).plusDays(1).toString(),
                        content: nameElement.html(),
                        type: "background",
                        style: "background-color: #DB4D4D20"
                    });
                }
            }
        });

        if (unassignedJobsCount === 0) {
            unassignedJobs.append($(`<p/>`).text(`There are no unassigned jobs.`));
        }

        // byCrewTimeline.setWindow(schedule.workCalendar.fromDate, schedule.workCalendar.toDate);
        byJobTimeline.setWindow(schedule.workCalendar.fromDate, schedule.workCalendar.toDate);
        byCapacityTimeline.setWindow(schedule.workCalendar.fromDate, schedule.workCalendar.toDate);
        
    });
}

function solve() {
    $.post("/schedule/solve", function () {
        refreshSolvingButtons(true);
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Start solving failed.", xhr);
    });
}

function refreshSolvingButtons(solving) {
    if (solving) {
        $("#solveButton").hide();
        $("#stopSolvingButton").show();
        if (autoRefreshIntervalId == null) {
            autoRefreshIntervalId = setInterval(refreshSchedule, 2000);
        }
    } else {
        $("#solveButton").show();
        $("#stopSolvingButton").hide();
        if (autoRefreshIntervalId != null) {
            clearInterval(autoRefreshIntervalId);
            autoRefreshIntervalId = null;
        }
    }
}

function stopSolving() {
    $.post("/schedule/stopSolving", function () {
        refreshSolvingButtons(false);
        refreshSchedule();
    }).fail(function (xhr, ajaxOptions, thrownError) {
        showError("Stop solving failed.", xhr);
    });
}

function addMinutes(date, minutes) {
    return new Date(date.getTime() + minutes*60000);
}

function analyze() {
    new bootstrap.Modal("#scoreAnalysisModal").show()
    const scoreAnalysisModalContent = $("#scoreAnalysisModalContent");
    scoreAnalysisModalContent.children().remove();
    scoreAnalysisModalContent.text("Analyzing score...");
    $.put("/schedule/analyze", JSON.stringify(loadedSchedule), function (scoreAnalysis) {
      scoreAnalysisModalContent.children().remove();
      scoreAnalysisModalContent.text("");
  
      const analysisTable = $(`<table class="table table-striped"/>`);
      const analysisTHead = $(`<thead/>`)
        .append($(`<tr/>`)
          .append($(`<th>Constraint</th>`))
          .append($(`<th>Score</th>`)));
      analysisTable.append(analysisTHead);
      const analysisTBody = $(`<tbody/>`)
      $.each(scoreAnalysis.constraints, (index, constraintAnalysis) => {
        analysisTBody.append($(`<tr/>`)
          .append($(`<td/>`).text(constraintAnalysis.name))
          .append($(`<td/>`).text(constraintAnalysis.score)));
      });
      analysisTable.append(analysisTBody);
      scoreAnalysisModalContent.append(analysisTable);
    }).fail(function (xhr, ajaxOptions, thrownError) {
      showError("Analyze failed.", xhr);
    },
    "text");
  }

  function setupAjax() {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json,text/plain', // plain text is required by solve() returning UUID of the solver job
        }
    });

    // Extend jQuery to support $.put() and $.delete()
    jQuery.each(["put", "delete"], function (i, method) {
        jQuery[method] = function (url, data, callback, type) {
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }
            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });
}

function updateJob(item) {
    $.put("/schedule/job", JSON.stringify(item), function (result) {
        console.log(result);
    }).done(function(data, statusText, xhr){
        if (xhr.status == 200) {
            Swal.fire("Wijziging opgeslagen!", "", "success")
            .then(function() {
                refreshSchedule();
            });
        }
    }).fail(function(data, textStatus, xhr) {
        Swal.fire("Systeemfout", "", "error");
   });
}

function removeJob(item) {
    let clearedItem = item;
    clearedItem.start = null;
    clearedItem.end = null;
    clearedItem.group = null;
    $.put("/schedule/job", JSON.stringify(item), function (result) {
        console.log(result);
    }).done(function(data, statusText, xhr){
        if (xhr.status == 200) {
            Swal.fire("Wijziging opgeslagen!", "", "success")
            .then(function() {
                refreshSchedule();
            });
        }
    }).fail(function(data, textStatus, xhr) {
        Swal.fire("Systeemfout", "", "error");
   });
}