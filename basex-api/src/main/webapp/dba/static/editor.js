/**
 * Opens a file.
 * @param {string} file optional file name
 */
function openFile(file) {
  if(_editor.getValue().trim() && !confirm("Replace editor contents?")) return;

  var name = file || fileName();
  return request("editor-open?name=" + encodeURIComponent(name)).then((text) => {
    _editor.setValue(text);
    finishFile(name, "File was opened.");
  }).catch((response) => {
    showError(response, name);
  });
}

/**
 * Saves a file.
 */
function saveFile() {
  // append file suffix
  var name = fileName();
  if(!name.includes(".")) name += ".xq";
  if(fileExists(name) && !confirm("Overwrite existing file?")) return;

  var fileString = document.getElementById("editor").value;
  return request("editor-save?name=" + encodeURIComponent(name), fileString).then((text) => {
    finishFile(name, "File was saved.");
    refreshDataList(text.split("/"));
  }).catch((response) => {
    showError(response, name);
  });
}

/**
 * Closes a file.
 */
function closeFile() {
  var name = fileName();
  return name ? request("editor-close?name=" + encodeURIComponent(name)).then((text) => {
    _editor.setValue("");
    finishFile("", "File was closed.");
    refreshDataList(text.split("/"));
  }).catch((response) => {
    showError(response);
  }) : Promise.resolve();
}

/**
 * Finishes a file operation.
 * @param {string} name new filename
 * @param {string} info info message
 */
function finishFile(name, info) {
  document.getElementById("file").value = name;
  var disabled = name && !name.match(/\.xq(m|l|uery)?$/i);
  document.getElementById("run").disabled = disabled;
  checkButtons();
  setText(info, "info");
  _editor.focus();
}

/**
 * Refreshes the list of editable files.
 * @param {array} names editable files
 */
function refreshDataList(names) {
  var files = document.getElementById("files");
  while(files.firstChild) {
    files.removeChild(files.firstChild);
  }
  for(var name of names) {
    var opt = document.createElement("option");
    opt.value = name;
    files.appendChild(opt);
  }
}

/**
 * Refreshes the editor buttons.
 */
function checkButtons() {
  var name = fileName();
  document.getElementById("open").disabled = !fileExists(name);
  document.getElementById("save").disabled = !name;
  document.getElementById("close").disabled = !name;
}

/**
 * Checks if the specified file exists.
 * @param {string} filename
 * @returns {boolean} result of check
 */
function fileExists(filename) {
  for(var file of document.getElementById("files").children) {
    if(file.value === filename) return true;
  }
  return false;
}

/**
 * Returns the current file name without file suffix
 * @returns {string} file name
 */
function fileName() {
  return document.getElementById("file").value.trim();
}
