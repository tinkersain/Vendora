$htmlPath = "C:\Users\tankar\Downloads\Vendora\vendora_documentation.html"
$sw = [System.IO.StreamWriter]::new($htmlPath, $false, [System.Text.Encoding]::UTF8)

# 1. HTML Header and Styles with Enhanced Layout & Print Optimization
$sw.WriteLine(@"
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Vendora Platform — Comprehensive Technical Architecture & Master Project Documentation</title>
    <script src="mermaid.min.js"></script>
    <script>
        window.onload = function() {
            mermaid.initialize({
                startOnLoad: true,
                theme: 'neutral',
                securityLevel: 'loose',
                er: { useMaxWidth: false },
                flowchart: { useMaxWidth: false, htmlLabels: true },
                sequence: { useMaxWidth: false, showSequenceNumbers: true },
                gantt: { useMaxWidth: false, fontSize: 11, barHeight: 18, barGap: 3 }
            });
        };
    </script>
    <style>
        @page {
            size: A4 portrait;
            margin: 12mm 12mm 12mm 12mm;
        }
        @media print {
            body {
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
            }
            .page-break {
                page-break-after: always;
                break-after: page;
            }
            .no-break {
                page-break-inside: avoid;
                break-inside: avoid;
            }
            table {
                page-break-inside: auto;
            }
            tr {
                page-break-inside: avoid;
                page-break-after: auto;
            }
            thead {
                display: table-header-group;
            }
            h1 {
                page-break-before: always;
                break-before: page;
            }
            h1.no-break-before {
                page-break-before: avoid;
                break-before: avoid;
            }
        }
        * {
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            font-size: 9.5pt;
            line-height: 1.48;
            color: #1e293b;
            background-color: #ffffff;
            margin: 0;
            padding: 0;
        }
        
        /* Cover Page Styling */
        .cover-page {
            height: 94vh;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 25px 15px;
            page-break-after: always;
            break-after: page;
        }
        .cover-header {
            border-bottom: 3px solid #2563eb;
            padding-bottom: 18px;
        }
        .cover-brand {
            font-size: 36pt;
            font-weight: 800;
            letter-spacing: -1px;
            color: #0f172a;
            margin: 0;
            text-transform: uppercase;
        }
        .cover-motto {
            font-size: 13pt;
            font-weight: 500;
            color: #2563eb;
            margin-top: 4px;
            letter-spacing: 0.5px;
        }
        .cover-title-block {
            margin: 25px 0;
        }
        .cover-title {
            font-size: 23pt;
            font-weight: 700;
            line-height: 1.25;
            color: #0f172a;
            margin-bottom: 10px;
        }
        .cover-subtitle {
            font-size: 11.5pt;
            color: #475569;
            line-height: 1.45;
        }
        .cover-meta-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            font-size: 9.5pt;
        }
        .cover-meta-table td {
            padding: 6px 10px;
            border-bottom: 1px solid #e2e8f0;
        }
        .cover-meta-table td.label {
            font-weight: 600;
            color: #0f172a;
            width: 32%;
            background-color: #f8fafc;
        }
        .cover-footer {
            border-top: 1px solid #cbd5e1;
            padding-top: 10px;
            font-size: 8.5pt;
            color: #64748b;
            display: flex;
            justify-content: space-between;
        }
        
        /* Headings */
        h1 {
            font-size: 17pt;
            font-weight: 700;
            color: #0f172a;
            margin-top: 20px;
            margin-bottom: 10px;
            padding-bottom: 5px;
            border-bottom: 2px solid #2563eb;
        }
        h2 {
            font-size: 12.5pt;
            font-weight: 600;
            color: #1e293b;
            margin-top: 16px;
            margin-bottom: 6px;
            padding-bottom: 3px;
            border-bottom: 1px solid #e2e8f0;
        }
        h3 {
            font-size: 11pt;
            font-weight: 600;
            color: #334155;
            margin-top: 12px;
            margin-bottom: 5px;
        }
        h4 {
            font-size: 10pt;
            font-weight: 600;
            color: #475569;
            margin-top: 8px;
            margin-bottom: 4px;
        }
        p {
            margin: 0 0 7px 0;
            text-align: justify;
        }
        ul, ol {
            margin: 0 0 8px 0;
            padding-left: 20px;
        }
        li {
            margin-bottom: 2.5px;
        }
        
        /* Tables */
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 8px 0;
            font-size: 8pt;
            line-height: 1.3;
        }
        th {
            background-color: #0f172a;
            color: #ffffff;
            font-weight: 600;
            text-align: left;
            padding: 5px 7px;
            border: 1px solid #0f172a;
        }
        td {
            padding: 4px 6px;
            border: 1px solid #cbd5e1;
            vertical-align: top;
        }
        tr:nth-child(even) td {
            background-color: #f8fafc;
        }
        
        /* Badges */
        .badge {
            display: inline-block;
            padding: 1.5px 5px;
            font-size: 7pt;
            font-weight: 700;
            border-radius: 4px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .badge-completed {
            background-color: #dcfce7;
            color: #15803d;
            border: 1px solid #86efac;
        }
        .badge-progress {
            background-color: #fef3c7;
            color: #b45309;
            border: 1px solid #fde68a;
        }
        .badge-notstarted {
            background-color: #ffe4e6;
            color: #be123c;
            border: 1px solid #fecdd3;
        }
        
        /* Code & Preformatted */
        pre {
            background-color: #f8fafc;
            border: 1px solid #e2e8f0;
            border-left: 3px solid #2563eb;
            border-radius: 4px;
            padding: 7px 9px;
            font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
            font-size: 7.5pt;
            line-height: 1.35;
            overflow-x: auto;
            margin: 6px 0;
            page-break-inside: avoid;
            break-inside: avoid;
        }
        code {
            font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
            font-size: 8pt;
            background-color: #f1f5f9;
            padding: 1px 3px;
            border-radius: 3px;
            color: #0f172a;
        }
        pre code {
            background-color: transparent;
            padding: 0;
            border-radius: 0;
            color: inherit;
        }
        
        /* Callouts / Notes */
        .callout {
            background-color: #eff6ff;
            border-left: 4px solid #3b82f6;
            padding: 7px 10px;
            margin: 8px 0;
            border-radius: 0 4px 4px 0;
            font-size: 8.5pt;
            page-break-inside: avoid;
            break-inside: avoid;
        }
        .callout-title {
            font-weight: 700;
            color: #1e40af;
            margin-bottom: 3px;
            display: block;
            font-size: 9pt;
        }
        
        /* Mermaid Diagram Containers */
        .mermaid-container {
            background-color: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            padding: 8px;
            margin: 10px 0;
            text-align: center;
            page-break-inside: avoid;
            break-inside: avoid;
        }
        .mermaid {
            display: inline-block;
            margin: 0 auto;
        }
        
        /* Table of Contents */
        .toc-list {
            list-style: none;
            padding-left: 0;
            margin: 14px 0;
        }
        .toc-item {
            display: flex;
            justify-content: space-between;
            padding: 4px 0;
            border-bottom: 1px dotted #cbd5e1;
            font-size: 9.5pt;
        }
        .toc-item.level-1 {
            font-weight: 700;
            color: #0f172a;
            margin-top: 5px;
            border-bottom: 1px solid #94a3b8;
        }
        .toc-item.level-2 {
            padding-left: 16px;
            color: #334155;
            font-weight: 500;
        }
    </style>
</head>
<body>
"@)
$sw.Flush()
$sw.Close()
Write-Host "Updated Header and CSS for 30-page precision."
