<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bibtex="http://bibtexml.sf.net/"
  xmlns="http://www.w3.org/1999/xhtml" >
  <xsl:output method="html" indent="yes" encoding="utf-8"/>


<!-- Indexation des éléments indi par leur attribut id -->
 <xsl:key name="idIndividu" match="indi" use="@id"/>

 <xsl:template match="/">
    <html> 
    <meta charset="utf-8" />
    <link rel="stylesheet" href="default.css" />
      <head>
        <title>Famille Royale</title>
      </head>
      <body>
        

        <h1><xsl:text>Liste des familles </xsl:text></h1>
        <xsl:apply-templates select="gedcom/fam" >
         <!-- Nous affichons les familles triees selon les maris -->
         <xsl:sort select="husb" />
        </xsl:apply-templates>



       <h1><xsl:text>Liste des individus </xsl:text></h1>
        <xsl:apply-templates select="gedcom/indi" >
          <!-- Nous affichons les individus selon l'ordre lexicographique effectue sur leur nom -->
          <xsl:sort select="name/lastname" order="ascending"/>
        </xsl:apply-templates>


         <!--   défault -->
        <xsl:apply-templates select="gedcom/*" >
        </xsl:apply-templates>

        </body>
    </html>
  </xsl:template>
    


  <!-- 
  ************************  FAMILLE  ************************ 
  -->
 <xsl:template match="gedcom/fam">

    <xsl:variable name="family" select="@id"/>
      <span id="{$family}">
          <br/><h3>Famille <xsl:value-of select="@id"/> : </h3>
        </span>
      <xsl:apply-templates/> 
  <xsl:apply-templates select="gedcom/fam/husb"/>
</xsl:template>

 <xsl:template match="husb">
   <xsl:variable name="husband" select="@ref"/>
   <i><xsl:text >Époux :  </xsl:text></i> 
  <a href="#{$husband}"><xsl:value-of select="key('idIndividu',$husband)/name"/></a>
  <!-- Affichage du "name" de l'individu au lieu de son identifiant -->
  <br/>
  </xsl:template>


  <xsl:template match="wife">
    <xsl:variable name="wife" select="@ref"/>
    <i><xsl:text >Épouse :  </xsl:text></i> 
     <a href="#{$wife}"><xsl:value-of select="key('idIndividu',$wife)/name"/></a>
  <br/> 
  </xsl:template>

  <xsl:template match="chil">
    <xsl:variable name="child" select="@ref"/>
  <ul><i><xsl:text >Enfant :  </xsl:text></i> 
       <a href="#{$child}"><xsl:value-of select="key('idIndividu',$child)/name"/></a>
  </ul>  
  </xsl:template>

  <!-- Evenement married -->
  <xsl:template match="marr">
  <xsl:text>date  : </xsl:text> 
          <xsl:value-of select="date"/>
  <xsl:call-template name="refPlace">
          <xsl:with-param name="node" select="plac"/>
  </xsl:call-template >
  <br/>
  </xsl:template>

  <!-- objets multimédias d'un individu -->
  <xsl:template match="fam/obje">
    <xsl:text>Objets : </xsl:text> 
    <xsl:value-of select="file"/>
  </xsl:template>

  <!-- Evenement divorce -->
  <xsl:template match="div">
  <xsl:text>Divorcé : </xsl:text>
  <xsl:variable name="var" as="@isdiv" />
   
   <xsl:if test="@isdiv='Y'" >
    <xsl:text> OUI </xsl:text>
   </xsl:if>

   <xsl:if test="@isdiv='N'" >
    <xsl:text> Non </xsl:text>
   </xsl:if>

  <br/>
  </xsl:template>
  
  <!-- autres -->
  <xsl:template match="*">
  </xsl:template>




  <!-- 
  ************************  INDIVIDUS  ************************ 
  -->
<xsl:template match="gedcom/indi">

<div id="{@id}" class="divIndi">
 <!-- id={@id} permet d'affecter l'identifiant de l'individu a la ligne
 ce qui permettra lorsque nous cliquerons sur un individu dans une famille de 
 nous positionner sur lui -->
  <xsl:call-template name="refIndi">
         <xsl:with-param name="node" select="@id"/>
  </xsl:call-template>
  <br/>
  
  <!-- titre individu -->
  <xsl:apply-templates select="titl"/>
  <br/>


  <!-- l'event sexe d'un individu -->
  <xsl:apply-templates select="sex"/>
  <div class="naissance"> 
     <xsl:apply-templates select="birt"/>
  </div>
  <div class="famsc"> 
     <xsl:apply-templates select="fams|famc"/>
  </div>
  <div class="others_evts"> 
  </div>
      <xsl:apply-templates select="deat|buri|chr|obje"/>
  </div>
</xsl:template>

 <!-- l'event sexe d'un individu -->
  <xsl:template match="sex">
  <xsl:text>Sexe : </xsl:text>
  <xsl:value-of select="@type"/>
  </xsl:template> 

     <!-- l'evenement naissance d'un individu -->
   <xsl:template match="birt"> 
    <xsl:text>Naissance ( </xsl:text> 
    <xsl:text>date  : </xsl:text> 
            <xsl:value-of select="date"/>
    <xsl:call-template name="refPlace">
            <xsl:with-param name="node" select="plac"/>
    </xsl:call-template > 
    <xsl:text>)</xsl:text>
    <br/>
  </xsl:template>




  
   <!-- l'evenement mort d'un individu -->
  <xsl:template match="deat">
    <xsl:text>Mort ( </xsl:text> 
    <xsl:text>date  : </xsl:text> 
            <xsl:value-of select="date"/>
    <xsl:call-template name="refPlace">
            <xsl:with-param name="node" select="plac"/>
    </xsl:call-template >
    <xsl:text>)</xsl:text>
    <br/>
  </xsl:template>

 
  <!-- l'evenement enterrement d'un individu -->
  <xsl:template match="buri">
    <xsl:text>Enterrement ( </xsl:text> 
    <xsl:text>date  : </xsl:text> 
            <xsl:value-of select="date"/>
    <xsl:call-template name="refPlace">
            <xsl:with-param name="node" select="plac"/>
    </xsl:call-template >
    <xsl:text>)</xsl:text>
  </xsl:template>

 <!--  baptème d'un individu -->
  <xsl:template match="chr">
    <xsl:text>Baptème ( </xsl:text> 
    <xsl:text>date  : </xsl:text> 
            <xsl:value-of select="date"/>
    <xsl:call-template name="refPlace">
            <xsl:with-param name="node" select="plac"/>
    </xsl:call-template >
    <xsl:text> ) </xsl:text> 
  </xsl:template>  


  <!-- objets multimédias d'un individu -->
  <xsl:template match="indi/obje">
    <xsl:text>Objets : </xsl:text> 
    <xsl:value-of select="file"/>
  </xsl:template>

  <!-- Titre d'un individu -->
  <xsl:template match="titl">
    <xsl:text>Titre : </xsl:text> 
    <xsl:value-of select="."/>
  </xsl:template>


  <!-- le famc d'un individu -->
  <xsl:template match="famc">
    <xsl:text>  Famc : </xsl:text> 
      <xsl:call-template name="refFamily">
        <xsl:with-param name="node" select="@ref"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Template a travers lequel nous allons traiter le fams d'un individu -->
  <xsl:template match="fams">
    <xsl:text>  Fams : </xsl:text> 
      <xsl:call-template name="refFamily">
            <xsl:with-param name="node" select="@ref"/>
      </xsl:call-template>
  </xsl:template>




  <!-- Template pour faire reference a un individu -->
  <xsl:template name="refIndi">
    <xsl:param name="node"/>
      <!-- la reference basee sur l'id de l'individu est "dissimulee" sous le nom et le pren de l'individu -->

     <xsl:if test="boolean(key('idIndividu',$node)/name/lastname) ='true'">
          <xsl:text>  Nom  : </xsl:text>
          <a href="#{$node}">
              <xsl:value-of select=" key('idIndividu',$node)/name/lastname"/>
          </a>
      </xsl:if>

     <xsl:if test="boolean(key('idIndividu',$node)/name/firstname) ='true'">
          <xsl:text>  Prénom  : </xsl:text>
          <a href="#{$node}">
              <xsl:value-of select=" key('idIndividu',$node)/name/firstname"/>
          </a>
      </xsl:if>

  </xsl:template>

  <!-- Template pour faire reference a une famille -->
  <xsl:template name="refFamily">
    <xsl:param name="node"/>
  <!-- href="#{node}" permet a famc et fams de faire reference a une famille
  ayant un identifiant egal au contenu de node et placee sur la meme page -->
  <a href="#{$node}">
    <xsl:value-of select=" $node"/>
    </a>  
  </xsl:template>

  <!-- Template pour faire reference a une place -->
  <xsl:template name="refPlace">
    <xsl:param name="node"/>  
  <xsl:text>, lieu  : </xsl:text> 
        <xsl:value-of select=" $node"/> 
  </xsl:template>


  

</xsl:stylesheet>