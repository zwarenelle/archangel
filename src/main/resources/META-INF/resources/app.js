var autoRefreshIntervalId = null;
var formattingOptions = 
{
    minorLabels: {
        millisecond:'',
        second:     '',
        minute:     'HH:mm',
        hour:       'HH:mm',
        weekday:    '',
        day:        '',
        week:       '',
        month:      '',
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
    xss: {disabled: true}, // Items are XSS safe through JQuery
    zoomMin: 1000 * 60 * 60 * 24, // One day in milliseconds
    locale: 'nl',
    format: formattingOptions
};

var byCrewGroupDataSet = new vis.DataSet();
var byCrewItemDataSet = new vis.DataSet();
var byCrewTimeline = new vis.Timeline(byCrewPanel, byCrewItemDataSet, byCrewGroupDataSet, byCrewTimelineOptions);

const byJobPanel = document.getElementById("byJobPanel");
const byJobTimelineOptions = {
    timeAxis: {scale: "hour", step: 6},
    orientation: {axis: "top"},
    xss: {disabled: true}, // Items are XSS safe through JQuery
    zoomMin: 1000 * 60 * 60 * 24, // One day in milliseconds
    locale: 'nl',
    format: formattingOptions
};

var byJobGroupDataSet = new vis.DataSet();
var byJobItemDataSet = new vis.DataSet();
var byJobTimeline = new vis.Timeline(byJobPanel, byJobItemDataSet, byJobGroupDataSet, byJobTimelineOptions);

const byCapacityPanel = document.getElementById("byCapacityPanel");
const byCapacityTimelineOptions = {
    timeAxis: {scale: "hour", step: 6},
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
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });

    $("#refreshButton").click(function () {
        refreshSchedule();
    });
    $("#solveButton").click(function () {
        solve();
    });
    $("#stopSolvingButton").click(function () {
        stopSolving();
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

        // $.each(schedule.crewList, (index, crew) => {
        //         const monteurDescription = $(`<div/>`)
        //         $.each(crew.monteurs, (index, monteur) => {
        //             monteurDescription.append(monteur.naam)
        //         });
        //         byCapacityGroupDataSet.add({id : monteur.id, content: monteurDescription.html()
        //     });
        // });

        $.each(schedule.crewList, (index, crew) => {
                const crewDescription = $(`<div/>`)
                .append(crew.name)
                $.each(crew.monteurs, (index, monteur) => {
                    crewDescription.append(`</br>`)
                    crewDescription.append(monteur.naam)
                    crewDescription.append(` `)
                    crewDescription.append(monteur.vaardigheid.omschrijving)
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
            byJobItemDataSet.add({
                  id: job.id + "_readyToIdealEnd", group: job.id,
                  start: job.readyDate, end: job.idealEndDate,
                  type: "background",
                  style: "background-color: #8AE23433"
            });
            byJobItemDataSet.add({
                  id: job.id + "_idealEndToDue", group: job.id,
                  start: job.idealEndDate, end: job.dueDate,
                  type: "background",
                  style: "background-color: #FCAF3E33"
            });

            if (job.crew == null || job.startDate == null) {
                unassignedJobsCount++;
                const unassignedJobElement = $(`<div class="card-body p-2"/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(job.adres))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`${job.durationInHours} uur`))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`Voorbereiding gereed: ${job.readyDate}`))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`Uiterlijke einddatum: ${job.dueDate}`));
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
                byJobItemDataSet.add({
                    id : job.id, group: job.id,
                    content: byJobJobElement.html(),
                    start: job.readyDate, end: JSJoda.LocalDateTime.parse(job.readyDate).plusDays(job.durationInHours).toString(),
                    style: "background-color: #EF292999"
                });
            } else {
                const beforeReady = JSJoda.LocalDateTime.parse(job.startDate).isBefore(JSJoda.LocalDateTime.parse(job.readyDate));
                const afterDue = JSJoda.LocalDateTime.parse(job.endDate).isAfter(JSJoda.LocalDateTime.parse(job.dueDate));
                const byCrewJobElement = $(`<div/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(job.adres))
                    .append($(`<p class="card-text ms-2 mb-0"/>`).text(`${job.durationInHours} uur`));
                const byJobJobElement = $(`<div/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(job.crew.name));
                if (beforeReady) {
                    byCrewJobElement.append($(`<p class="badge badge-danger mb-0"/>`).text(`Before ready (too early)`));
                    byJobJobElement.append($(`<p class="badge badge-danger mb-0"/>`).text(`Before ready (too early)`));
                }
                if (afterDue) {
                    byCrewJobElement.append($(`<p class="badge badge-danger mb-0"/>`).text(`After due (too late)`));
                    byJobJobElement.append($(`<p class="badge badge-danger mb-0"/>`).text(`After due (too late)`));
                }
                $.each(job.requiredSkills, (index, tag) => {
                    if (tag.omschrijving.toString().startsWith("VIAG"))
                    {
                        color = "#FEB900";
                    }
                    else if (tag.omschrijving.toString().startsWith("BEI"))
                    {
                        color = "#ED5353";
                    }
                    else
                    {
                        color = "#003366";
                    }
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
        if (unassignedJobsCount === 0) {
            unassignedJobs.append($(`<p/>`).text(`There are no unassigned jobs.`));
        }

        byCrewTimeline.setWindow(schedule.workCalendar.fromDate, schedule.workCalendar.toDate);
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