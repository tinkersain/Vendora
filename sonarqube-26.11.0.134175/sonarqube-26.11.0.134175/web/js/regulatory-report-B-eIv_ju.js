/*! licenses: /vendor.LICENSE.txt */
function e(e,t){let n=new URLSearchParams({project:e});return t&&n.append(`branch`,t),`/api/regulatory_reports/download?${n.toString()}`}export{e as t};