const { DateTime } = luxon;

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
    timeAxis: {scale: "hour", step: 1},
    orientation: {axis: "top"},
    stack: false,
    editable: {
        add: false,         // add new items by double tapping
        updateTime: true,  // drag items horizontally
        updateGroup: true, // drag items from one group to another
        remove: true,       // delete an item by tapping the delete button top right
        overrideItems: false  // allow these options to override item.editable
      },    
    multiselect: true,
    xss: {disabled: true}, // Items are XSS safe through JQuery
    zoomMin: 1000 * 60 * 60, // One hour in milliseconds
    locale: 'nl',
    // cluster: {
    //     maxItems: 3,
    //     titleTemplate: null,
    //     clusterCriteria: null,
    //     showStipes: false,
    //     fitOnDoubleClick: true,
    // },
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
            if (result.isConfirmed) {
                callback(item);
                removeOpdracht(item);
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
            if (result.isConfirmed) {
                callback(item);
                updateOpdracht(item);
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

const byOpdrachtPanel = document.getElementById("byOpdrachtPanel");
const byOpdrachtTimelineOptions = {
    timeAxis: {scale: "hour", step: 6},
    orientation: {axis: "top"},
    xss: {disabled: true},
    zoomMin: 1000 * 60 * 60,
    locale: 'nl',
    format: formattingOptions
};

var byOpdrachtGroupDataSet = new vis.DataSet();
var byOpdrachtItemDataSet = new vis.DataSet();
var byOpdrachtTimeline = new vis.Timeline(byOpdrachtPanel, byOpdrachtItemDataSet, byOpdrachtGroupDataSet, byOpdrachtTimelineOptions);

const byCapacityPanel = document.getElementById("byCapacityPanel");
const byCapacityTimelineOptions = {
    timeAxis: {scale: "day", step: 1},
    orientation: {axis: "top"},
    xss: {disabled: true},
    zoomMin: 1000 * 60 * 60 * 24,
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
    $("#byOpdrachtTab").on('shown.bs.tab', function (event) {
        byOpdrachtTimeline.redraw();
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

        const unassignedOpdrachts = $("#unassignedOpdrachts");
        unassignedOpdrachts.children().remove();
        var unassignedOpdrachtsCount = 0;
        byCrewGroupDataSet.clear();
        byOpdrachtGroupDataSet.clear();
        byCapacityGroupDataSet.clear();
        byCrewItemDataSet.clear();
        byOpdrachtItemDataSet.clear();
        byCapacityItemDataSet.clear();

        // Map Monteur ID's to Crew ID's for later usage in availabilty background
        var MonteurToCrew = new Map();

        $.each(schedule.crewList, (index, crew) => {
            if (crew.id != 1) {
                const crewDescription = $(`<div/>`)
                .append(crew.naam)
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
                byCrewGroupDataSet.add({id : crew.id, content: crewDescription.html()});
            }
        });

        $.each(schedule.opdrachtList, (index, opdracht) => {
            if (opdracht.crew.id != 1) {
                const opdrachtGroupElement = $(`<div/>`)
                .append($(`<h5 class="card-title mb-1"/>`).text(opdracht.adres))
                .append($(`<p class="card-text ms-2 mb-0"/>`).text("Bestekcode: " + opdracht.bestekcode))
                .append($(`<p class="card-text ms-2 mb-0"/>`).text("Verwachte uitvoeringsduur: " + `${opdracht.durationInHours} uur`));
                $.each(opdracht.requiredSkills, (index, req) => {
                    opdrachtGroupElement.append(`</br>`)
                    opdrachtGroupElement.append(req.aantal)
                    opdrachtGroupElement.append(` `)
                    opdrachtGroupElement.append(req.omschrijving)
                });
                byOpdrachtGroupDataSet.add({
                    id : opdracht.id,
                    content: opdrachtGroupElement.html()
                });

                if (opdracht.crew == null || opdracht.startDate == null) {
                    unassignedOpdrachtsCount++;
                    const unassignedOpdrachtElement = $(`<div class="card-body p-2"/>`)
                        .append($(`<h5 class="card-title mb-1"/>`).text(opdracht.adres))
                        .append($(`<p class="card-text ms-2 mb-0"/>`).text(`Bestekcode: ${opdracht.bestekcode}`))
                        .append($(`<p class="card-text ms-2 mb-0"/>`).text(`Verwachte uitvoeringsduur: ${opdracht.durationInHours} uur`));
                    const byOpdrachtOpdrachtElement = $(`<div/>`)
                    .append($(`<h5 class="card-title mb-1"/>`).text(`Unassigned`));
                    $.each(opdracht.requiredSkills, (index, tag) => {
                        if (tag.omschrijving.toString().startsWith("VIAG"))
                        { color = "#FEB900"; }
                        else if (tag.omschrijving.startsWith("BEI"))
                        { color = "#ED5353"; }
                        else { color = "#003366"; }
                        unassignedOpdrachtElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                        byOpdrachtOpdrachtElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                    });
                    unassignedOpdrachts.append($(`<div class="col"/>`)
                        .append($(`<div class="card"/>`)
                        .append($(`<div class="container"/>`)
                        .append($(`<div class="row align-items-center"/>`)
                        .append($(`<div class="col-sm-8"/>`)
                        .append(unassignedOpdrachtElement))
                        .append($(`<div class="col-sm-4"/>`)
                        .append($(`<button type="button" id="` + opdracht.id + `" class="btn btn-outline-info">Plannen</button>`).on("click", function() { plannen(opdracht) })))
                        ))));
                            } else {
                    const byCrewOpdrachtElement = $(`<div/>`)
                        .append($(`<h5 class="card-title mb-1"/>`).text(opdracht.adres))
                        .append($(`<p class="card-text ms-2 mb-0"/>`).text(`${opdracht.bestekcode}: ${opdracht.durationInHours} uur`));
                    const byOpdrachtOpdrachtElement = $(`<div/>`)
                        .append($(`<h5 class="card-title mb-1"/>`).text(opdracht.crew.naam));
                    $.each(opdracht.requiredSkills, (index, tag) => {
                        if (tag.omschrijving.toString().startsWith("VIAG")) {color = "#FEB900";}
                        else if (tag.omschrijving.toString().startsWith("BEI")) {color = "#ED5353";}
                        else {color = "#003366";}
                        byCrewOpdrachtElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                        byOpdrachtOpdrachtElement.append($(`<span class="badge me-1" style="background-color: ${color}"/>`).text(tag.aantal + "x " + tag.omschrijving));
                    });
                    byCrewItemDataSet.add({
                        id : opdracht.id, group: opdracht.crew.id,
                        content: byCrewOpdrachtElement.html(),
                        start: opdracht.startDate, end: opdracht.endDate
                    });
                    byOpdrachtItemDataSet.add({
                        id : opdracht.id, group: opdracht.id,
                        content: byOpdrachtOpdrachtElement.html(),
                        start: opdracht.startDate, end: opdracht.endDate
                    });
                }
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
                start: beschikbaarheid.start, end: beschikbaarheid.end
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
                        start: beschikbaarheid.start, end: beschikbaarheid.end,
                        content: nameElement.html(),
                        type: "background",
                        style: "background-color: #DB4D4D20"
                    });
                }
            }
        });

        if (unassignedOpdrachtsCount === 0) {
            unassignedOpdrachts.append($(`<p/>`).text(`Geen opdrachten in voorraad.`));
        }
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
            'Accept': 'application/json,text/plain', // plain text is required by solve() returning UUID of the solver opdracht
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

function plannen(opdracht) {
    console.log("Received plan");
    console.log("opdrachtId: " + opdracht.id);

    var selected;

    $(document).on('change','.optradio',function() { // Listen for change on radio CSS class
        selected = $('input[name=optradio]:checked').val();
        console.log(selected);
    });

    $.getJSON("/schedule/opdracht/proposition", {id: opdracht.id}, function(data, statusText, xhr){
        if (xhr.status == 200) {
            console.log(data);
            var proposition = `
            <div class="d-flex justify-content-center">
            <table>
             <colgroup>
              <col span="1">
               <col style="border: 2px solid black">
              <col span="1">
             </colgroup>
             <thead>
              <tr>
                <th scope="col">Ploeg</th>
                <th scope="col">Begin</th>
                <th scope="col">Eind</th>
                <th scope="col">Score</th>
                <th scope="col">Keuze</th>
              </tr>
             </thead>
             <tbody>
            `;
            $.each(data, (index, element) => {
                proposition += `<tr>
                <th scope="row">`+ element.proposition.crew.naam + `</th>
                <td>`+ DateTime.fromISO(element.proposition.startDate).setLocale('nl').toLocaleString(DateTime.DATETIME_FULL) + `</td>
                <td>`+ DateTime.fromISO(element.proposition.endDate).setLocale('nl').toLocaleString(DateTime.DATETIME_FULL) + `</td>
                <td>`+ element.scoreDiff.score + `</td>
                <td>
                 <div class="radio">
                  <input type="radio" value=` + index + ` name="optradio" class='optradio'>
                 </div>
                </td>
              </tr>`;
            });
            proposition += `</tbody>
            </table>
            </div>`;
            Swal.fire({
                title: "Maak een keuze",
                showCancelButton: true,
                confirmButtonText: "Plannen",
                width: "100em",
                html: proposition, 
                icon: "info"})
            .then((result) => {
                if (result.isConfirmed) {
                    opdracht.group = data[selected].proposition.crew.id;
                    opdracht.start = DateTime.fromISO(data[selected].proposition.startDate).setZone('UTC').toFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    opdracht.end = DateTime.fromISO(data[selected].proposition.endDate).setZone('UTC').toFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    updateOpdracht(opdracht);
                }
            });
        }
    }).fail(function(data, textStatus, xhr) {
        Swal.fire("Fout bij ophalen van plan-mogelijkheden", "", "error");
   });
}

function updateOpdracht(item) {
    $.put("/schedule/opdracht", JSON.stringify(item), function(data, statusText, xhr){
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

function removeOpdracht(item) {
    let clearedItem = item;
    clearedItem.start = null;
    clearedItem.end = null;
    clearedItem.group = null;
    $.put("/schedule/opdracht", JSON.stringify(item), function(data, statusText, xhr){
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