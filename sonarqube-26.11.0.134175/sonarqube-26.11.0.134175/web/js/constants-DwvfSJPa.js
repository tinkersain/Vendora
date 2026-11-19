/*! licenses: /vendor.LICENSE.txt */
import{a as e}from"./rolldown-runtime-LzpV0vpq.js";import{C as t}from"./vendor-BinpZ4gA.js";import{r as n}from"./settings-DvyfV5ZN.js";import{o as r,u as i}from"./doc-links-Bzb1j9cf.js";import{a,i as o,n as s}from"./settings-C-95uQUI.js";var c=e(t()),l=`ai_codefix`,u=`authentication`,d=`exclusions`,f=`languages`,p=`email_notification`,m=`mode`,h=`early_access_features`,g=`jira_project_binding`,_={abap:f,ansible:f,apex:f,"azure pipelines":f,azureresourcemanager:f,"c / c++ / objective-c":f,"c#":f,cloudformation:f,cobol:f,css:f,dart:f,delphi:f,docker:f,flex:f,"github actions":f,go:f,gosu:f,groovy:f,html:f,java:f,javascript:f,"javascript / typescript":f,jcl:f,json:f,kotlin:f,kubernetes:f,mulesoft:f,php:f,"pl/i":f,"pl/sql":f,powershell:f,python:f,r:f,rpg:f,ruby:f,rust:f,secrets:f,scala:f,shell:f,sql:f,swift:f,"t-sql":f,terraform:f,typescript:f,"vb.net":f,"visual basic":f,xml:f,yaml:f},v=`sonar.dbcleaner.daysBeforeDeletingHistory`,y={general:[`email`]},b=/^[a-z0-9\-_]+$/,x={name:`Sandbox specific issue categories after SonarQube update`,description:``,category:`general`,key:`sonar.issues.sandbox.enabled`,fields:[],options:[],subCategory:`issues`},S=[{name:`DevOps Platform Integration`,description:`
      Display your Quality Gate status directly in your DevOps Platform.
      Each DevOps Platform instance must be configured globally first, and given a unique name. Pick the instance your project is hosted on.
      `,category:a,key:``,fields:[],options:[],subCategory:``},x],C=[{name:`Default New Code behavior`,description:`
        The New Code definition is used to compare measures and track new issues.
        This setting is the default for all projects. A specific New Code definition can be configured at project level.
      `,category:o,key:`sonar.new_code_period`,fields:[],options:[],subCategory:``},{name:`Azure DevOps integration`,description:`azure devops integration configuration
      Configuration name
      Give your configuration a clear and succinct name.
      This name will be used at project level to identify the correct configured Azure instance for a project.
      Azure DevOps URL
      For Azure DevOps Server, provide the full collection URL:
      https://ado.your-company.com/your_collection

      For Azure DevOps Services, provide the full organization URL:
      https://dev.azure.com/your_organization
      Personal Access Token
      SonarQube needs a Personal Access Token to report the Quality Gate status on Pull Requests in Azure DevOps.
      To create this token, we recommend using a dedicated Azure DevOps account with administration permissions.
      The token itself needs Code > Read & Write permission.
    `,category:s,key:`sonar.almintegration.${i.Azure}`,fields:[],options:[],subCategory:``},{name:`Bitbucket integration`,description:`bitbucket data center cloud integration configuration
      Configuration name
      Give your configuration a clear and succinct name.
      This name will be used at project level to identify the correct configured Bitbucket instance for a project.
      Bitbucket Data Center URL
      Example: https://bitbucket-datacenter.your-company.com
      Personal Access Token
      SonarQube needs a Personal Access Token to report the Quality Gate status on Pull Requests in Bitbucket Data Center.
      To create this token, we recommend using a dedicated Bitbucket Data Center account with administration permissions.
      The token itself needs Read permission.
      Workspace ID
      The workspace ID is part of your bitbucket cloud URL https://bitbucket.org/{workspace}/{repository}
      SonarQube needs you to create an OAuth client in your Bitbucket Cloud workspace settings
      to report the Quality Gate status on Pull Requests.
      It needs to be a private client with Pull Requests: Read permission.
      An OAuth callback URL is required by Bitbucket Cloud but not used by SonarQube so any URL works.
      Client ID
      Bitbucket generates the Client ID when you create your OAuth client.
      You can find it in your Bitbucket Cloud workspace settings under OAuth clients.
      Client Secret
      Bitbucket generates the Client Secret when you create your OAuth client.
      You can find it in your Bitbucket Cloud workspace settings under OAuth clients.
    `,category:s,key:`sonar.almintegration.${i.BitbucketServer}`,fields:[],options:[],subCategory:``},{name:`GitHub integration`,description:`github integration configuration
      Configuration name
      Give your configuration a clear and succinct name.
      This name will be used at project level to identify the correct configured GitHub App for a project.
      GitHub API URL
      Example for Github Enterprise:
      https://github.company.com/api/v3
      If using GitHub.com:
      https://api.github.com/
      You need to install a GitHub App with specific settings and permissions to enable
      Pull Request Decoration on your Organization or Repository.
      GitHub App ID
      The App ID is found on your GitHub App's page on GitHub at Settings > Developer Settings > GitHub Apps
      Client ID
      The Client ID is found on your GitHub App's page.
      Client Secret
      The Client secret is found on your GitHub App's page.
      Private Key
      Your GitHub App's private key. You can generate a .pem file from your GitHub App's page under Private keys.
      Copy and paste the whole contents of the file here.
    `,category:s,key:`sonar.almintegration.${i.GitHub}`,fields:[],options:[],subCategory:``},{name:`Gitlab integration`,description:`gitlab integration configuration
      Configuration name
      Give your configuration a clear and succinct name.
      This name will be used at project level to identify the correct configured GitLab instance for a project.
      GitLab API URL
      Provide the GitLab API URL. For example:
      https://gitlab.com/api/v4
      Personal Access Token
      SonarQube needs a Personal Access Token to report the Quality Gate status on Merge Requests in GitLab.
      To create this token,
      we recommend using a dedicated GitLab account with Reporter permission to all target projects.
      The token itself needs the api scope.
    `,category:s,key:`sonar.almintegration.${i.GitLab}`,fields:[],options:[],subCategory:``},x],w={[n.IssueResolutionEnabled]:n.IssueResolutionGlobalEnabled},T={[n.IssueResolutionEnabled]:r.IssuesManaging,[n.IssueResolutionGlobalEnabled]:r.IssuesManaging},E={[n.AutodetectAICode]:(e,t)=>t.formatMessage({id:`property.sonar.autodetect.ai.code.confirmation`},{value:e,p1:e=>(0,c.jsx)(`p`,{children:e}),p:e=>(0,c.jsx)(`p`,{className:`sw:mt-2`,children:e})})};export{y as _,u as a,v as c,p as d,g as f,w as g,E as h,d as i,T as l,m,C as n,b as o,f as p,l as r,_ as s,S as t,h as u};