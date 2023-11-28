function replaceTimefoldAutoHeaderFooter() {
  const timefoldHeader = $("header#timefold-auto-header");
  if (timefoldHeader != null) {
    timefoldHeader.addClass("bg-black")
    timefoldHeader.append(
      $(`<div class="container-fluid">
        <nav class="navbar sticky-top navbar-expand-lg navbar-dark shadow mb-3">
          <a class="navbar-brand" href="https://heijmans.nl">
            <svg xmlns="http://www.w3.org/2000/svg" width="111" height="28" viewBox="0 0 111 28" aria-hidden="true" data-testid="logo" class="icon icon--logo">
              <g fill="none" fill-rule="evenodd">
                  <path d="M42.574 6.721v.298c0 5.2.002 10.4-.001 15.6-.002 2.878-1.75 4.9-4.61 5.297-.503.07-1.023.01-1.552.01V24.94c.292-.03.574-.04.849-.09 1.106-.2 1.691-.91 1.748-2.102l.005-.19V6.722h3.56zm-8.815-.01v.375c0 3.084-.002 6.168.004 9.252 0 .299.024.603.089.894.196.889.778 1.382 1.689 1.464.269.024.538.039.822.059v3.076c-.65-.044-1.292-.043-1.919-.14-1.257-.193-2.36-.709-3.173-1.727-.712-.891-1.045-1.937-1.055-3.052-.032-3.346-.016-6.692-.017-10.038 0-.047.009-.093.016-.163h3.544zm76.375 1.445c.048.047.091.097.124.133l-2.096 2.08c-.128-.112-.285-.25-.445-.387-.833-.708-1.784-1.092-2.892-1.019a3.49 3.49 0 00-.688.114c-.554.151-.911.49-.996 1.084-.09.62.115 1.123.686 1.373.717.315 1.468.556 2.208.816.696.244 1.417.426 2.093.717 1.388.596 2.323 1.599 2.587 3.134.036.208.063.417.094.625v.583c-.049.285-.082.574-.149.855-.39 1.654-1.49 2.66-3.065 3.127-2.192.651-4.381.568-6.517-.283a5.19 5.19 0 01-2.014-1.417l1.768-2.402c1.194 1.338 2.709 1.81 4.421 1.748a3.898 3.898 0 001.107-.224c.606-.21.941-.668 1.006-1.305.07-.698-.095-1.317-.765-1.656-.482-.244-1-.421-1.511-.602-.97-.341-1.972-.605-2.914-1.01-1.164-.499-2.021-1.343-2.339-2.62-.512-2.055.436-3.936 2.423-4.84 2.527-1.149 5.888-.562 7.874 1.376zM24.882 7.2c1.477.91 2.257 2.312 2.33 4.045.053 1.289.017 2.58.02 3.871 0 .048-.014.095-.026.18H18.79c-.315 2.195.886 3.428 2.726 3.57 1.39.106 2.712-.157 3.931-.943l1.133 2.576c-.777.505-1.597.875-2.49 1.055-1.84.372-3.679.41-5.453-.302-2.097-.842-3.162-2.462-3.26-4.674-.074-1.665-.039-3.337-.021-5.006.022-2.082.993-3.648 2.824-4.584 2.242-1.145 4.533-1.125 6.702.212zm53.002-.625c2.01.631 3.041 2.083 3.298 4.122.054.43.06.87.062 1.305l.003 4.54v4.91h-2.433l-.486-1.1a4.146 4.146 0 01-2.348 1.317c-1.558.292-3.069.119-4.46-.67-1.331-.756-1.978-1.975-2.106-3.458-.053-.609-.042-1.25.091-1.844.35-1.555 1.414-2.519 2.851-3.107.902-.368 1.852-.511 2.821-.526.875-.012 1.75-.002 2.638-.002.142-1.34-.52-2.793-2.366-2.925a6.872 6.872 0 00-3.222.52c-.071.03-.145.054-.261.097l-.992-2.622c.652-.233 1.272-.524 1.925-.673 1.664-.382 3.342-.4 4.985.116zm-14.07.15c1.789.793 2.707 2.219 2.874 4.122.08.906.04 1.823.04 2.735l.003 3.726-.001 4.148h-3.578v-.35c0-3.183.002-6.366-.002-9.548 0-.25-.015-.506-.063-.751-.195-.984-.958-1.637-1.966-1.67a4.256 4.256 0 00-1.071.079c-1.026.233-1.639 1.043-1.643 2.152-.008 2.232-.003 4.464-.003 6.695v3.403h-3.561v-.368c0-3.163.002-6.327-.003-9.49 0-.26-.013-.526-.065-.78-.205-1.015-.968-1.667-2.012-1.691a4.184 4.184 0 00-1.18.125c-.87.237-1.42.955-1.477 1.852-.02.329-.023.66-.023.989l-.001 4.468v4.887h-3.553V6.72c.074-.006.157-.019.24-.02.702 0 1.403.007 2.103-.005.184-.003.282.058.354.222.107.245.237.48.363.731 2.005-2.067 6.11-1.796 7.52.16.06-.046.12-.088.175-.138 1.001-.913 2.172-1.415 3.534-1.452 1.032-.028 2.044.085 2.995.507zm29.989-.175c1.85.653 2.763 2.044 3.033 3.916.055.382.07.773.07 1.16l.004 4.76v5.068h-3.562v-.323c0-3.182.002-6.365-.003-9.548 0-.29-.012-.585-.073-.867-.182-.84-.693-1.399-1.557-1.519a4.455 4.455 0 00-1.5.034c-.997.21-1.605 1.017-1.61 2.047-.01 2.047-.004 4.095-.005 6.142v4.041h-3.555V6.7h1.143c.4 0 .799.01 1.197-.005.185-.006.281.06.353.225.104.239.232.467.364.726.139-.131.257-.254.387-.362a4.577 4.577 0 012.637-1.05c.911-.068 1.812.01 2.677.316zM5.425.05l.642.004c.027 0 .054.016.098.029V3.06c-.351.048-.692.069-1.02.143-.906.205-1.458.854-1.538 1.78a8.78 8.78 0 00-.035.755l-.002 7.654v8.068H.018c-.005-.098-.014-.191-.014-.285 0-5.277-.012-10.555.006-15.832C.016 3.484.71 1.937 2.32.895 3.258.287 4.309.037 5.425.05zm2.1 6.19c.873.055 1.709.268 2.475.7 1.396.786 2.1 2.028 2.31 3.574.054.393.069.793.07 1.19l.003 4.715v5.041H8.82v-.347c0-3.163.001-6.326-.003-9.49 0-.27-.02-.544-.072-.809-.175-.888-.778-1.417-1.69-1.503-.271-.026-.543-.04-.834-.06V6.237c.437 0 .872-.022 1.304.005zm67.89 8.413c-.213.001-.43 0-.64.034-1.429.229-2.076 1.002-2 2.371.058 1.014.647 1.744 1.596 1.92.35.064.716.077 1.072.058 1.263-.067 2.17-.78 2.328-1.96.105-.778.02-1.582.02-2.424-.783 0-1.58-.002-2.376.001zM20.32 9.272c-.631.128-1.082.554-1.335 1.156-.282.672-.253 1.378-.225 2.098h5.068c0-.363.008-.702-.003-1.04a3.732 3.732 0 00-.07-.606c-.202-1.01-.761-1.591-1.79-1.709-.54-.061-1.11-.008-1.645.1z" fill="currentColor"></path>
                  <path fill="#F70000" d="M30.184 3.028h12.312V.05H30.184z"></path>
              </g>
            </svg>          
          </a>
        </nav>
      </div>`));
  }
  const timefoldFooter = $("footer#timefold-auto-footer");
  if (timefoldFooter != null) {
    timefoldFooter.append(
      $(`<footer class="bg-black text-white-50">
           <div class="container">
             <div class="hstack gap-3 p-4">
               <div class="ms-auto"><a class="text-white" href="https://timefold.ai">Timefold</a></div>
               <div class="vr"></div>
               <div><a class="text-white" href="https://heijmans.nl">Heijmans</a></div>
               <div class="vr"></div>
               <div class="me-auto"><a class="text-white" href="https://github.com/zwarenelle/archangel">Github</a></div>
             </div>
           </div>
           <div id="applicationInfo" class="container text-center"></div>
         </footer>`));
  }

}

function showSimpleError(title) {
    const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 50rem"/>`)
        .append($(`<div class="toast-header bg-danger">
                 <strong class="me-auto text-dark">Error</strong>
                 <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
               </div>`))
        .append($(`<div class="toast-body"/>`)
            .append($(`<p/>`).text(title))
        );
    $("#notificationPanel").append(notification);
    notification.toast({delay: 30000});
    notification.toast('show');
}

function showError(title, xhr) {
  var serverErrorMessage = !xhr.responseJSON ? `${xhr.status}: ${xhr.statusText}` : xhr.responseJSON.message;
  var serverErrorCode = !xhr.responseJSON ? `unknown` : xhr.responseJSON.code;
  var serverErrorId = !xhr.responseJSON ? `----` : xhr.responseJSON.id;
  var serverErrorDetails = !xhr.responseJSON ? `no details provided` : xhr.responseJSON.details;

  if (xhr.responseJSON && !serverErrorMessage) {
	  serverErrorMessage = JSON.stringify(xhr.responseJSON);
	  serverErrorCode = xhr.statusText + '(' + xhr.status + ')';
	  serverErrorId = `----`;
  }

  console.error(title + "\n" + serverErrorMessage + " : " + serverErrorDetails);
  const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 50rem"/>`)
    .append($(`<div class="toast-header bg-danger">
                 <strong class="me-auto text-dark">Error</strong>
                 <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
               </div>`))
    .append($(`<div class="toast-body"/>`)
      .append($(`<p/>`).text(title))
      .append($(`<pre/>`)
        .append($(`<code/>`).text(serverErrorMessage + "\n\nCode: " + serverErrorCode + "\nError id: " + serverErrorId))
      )
    );
  $("#notificationPanel").append(notification);
  notification.toast({delay: 30000});
  notification.toast('show');
}

// ****************************************************************************
// TangoColorFactory
// ****************************************************************************

const SEQUENCE_1 = [0x8AE234, 0xFCE94F, 0x729FCF, 0xE9B96E, 0xAD7FA8];
const SEQUENCE_2 = [0x73D216, 0xEDD400, 0x3465A4, 0xC17D11, 0x75507B];

var colorMap = new Map;
var nextColorCount = 0;

function pickColor(object) {
  let color = colorMap[object];
  if (color !== undefined) {
    return color;
  }
  color = nextColor();
  colorMap[object] = color;
  return color;
}

function nextColor() {
  let color;
  let colorIndex = nextColorCount % SEQUENCE_1.length;
  let shadeIndex = Math.floor(nextColorCount / SEQUENCE_1.length);
  if (shadeIndex === 0) {
    color = SEQUENCE_1[colorIndex];
  } else if (shadeIndex === 1) {
    color = SEQUENCE_2[colorIndex];
  } else {
    shadeIndex -= 3;
    let floorColor = SEQUENCE_2[colorIndex];
    let ceilColor = SEQUENCE_1[colorIndex];
    let base = Math.floor((shadeIndex / 2) + 1);
    let divisor = 2;
    while (base >= divisor) {
      divisor *= 2;
    }
    base = (base * 2) - divisor + 1;
    let shadePercentage = base / divisor;
    color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
  }
  nextColorCount++;
  return "#" + color.toString(16);
}

function buildPercentageColor(floorColor, ceilColor, shadePercentage) {
  let red = (floorColor & 0xFF0000) + Math.floor(shadePercentage * ((ceilColor & 0xFF0000) - (floorColor & 0xFF0000))) & 0xFF0000;
  let green = (floorColor & 0x00FF00) + Math.floor(shadePercentage * ((ceilColor & 0x00FF00) - (floorColor & 0x00FF00))) & 0x00FF00;
  let blue = (floorColor & 0x0000FF) + Math.floor(shadePercentage * ((ceilColor & 0x0000FF) - (floorColor & 0x0000FF))) & 0x0000FF;
  return red | green | blue;
}
