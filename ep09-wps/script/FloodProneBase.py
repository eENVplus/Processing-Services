# -*- coding: utf-8 -*-
# ---------------------------------------------------------------------------
# FloodProneBase.py
# Created on: 2014-02-20 by Carlo Cipolloni, Maria Pia Congi and Marco Pantaloni  - Geological Survey of Italy ISPRA
#   (generated by ArcGIS/ModelBuilder)
# Usage: FloodProneBase <Alluvium_Deposits> <Harmonised_Geoloigcal_Map> <SlopeDegree> <Dem40_FVG> <SlopeFlatClass> <AlluviumClass> <HighProne> <LowProne> <LowPotential> <HighPotential> 
# Description: That procedure calculates the prone flood area based on the geology and morphometric parameters (slope degree) for test has been elaborated on the Dem 40m cell, but could be performed with other detail.
#				To select the geologic data the attribute and layer could be changed on the base of input WFS geology service; 
#				after the interaction procedure the users should be able to compare Flood Potential zone map with topography and with PAIs data/service.
# ---------------------------------------------------------------------------

# Import arcpy module
import arcpy

# Check out any necessary licenses
arcpy.CheckOutExtension("spatial")

# Script arguments
Alluvium_Deposits = arcpy.GetParameterAsText(0)
if Alluvium_Deposits == '#' or not Alluvium_Deposits:
    Alluvium_Deposits = ".//..//select_geology" # provide a default value if unspecified

Harmonised_Geoloigcal_Map = arcpy.GetParameterAsText(1)
if Harmonised_Geoloigcal_Map == '#' or not Harmonised_Geoloigcal_Map:
    Harmonised_Geoloigcal_Map = ".//..//select_onege_fvg" # provide a default value if unspecified

SlopeDegree = arcpy.GetParameterAsText(2)
if SlopeDegree == '#' or not SlopeDegree:
    SlopeDegree = ".//..//Slope40" # provide a default value if unspecified

Dem40_FVG = arcpy.GetParameterAsText(3)
if Dem40_FVG == '#' or not Dem40_FVG:
    Dem40_FVG = ".//..//Dem40_FVG" # provide a default value if unspecified

SlopeFlatClass = arcpy.GetParameterAsText(4)
if SlopeFlatClass == '#' or not SlopeFlatClass:
    SlopeFlatClass = ".//..//SlopeFlat" # provide a default value if unspecified

AlluviumClass = arcpy.GetParameterAsText(5)
if AlluviumClass == '#' or not AlluviumClass:
    AlluviumClass = ".//..//AlluvClass" # provide a default value if unspecified

HighProne = arcpy.GetParameterAsText(6)
if HighProne == '#' or not HighProne:
    HighProne = ".//..//HProne" # provide a default value if unspecified

LowProne = arcpy.GetParameterAsText(7)
if LowProne == '#' or not LowProne:
    LowProne = ".//..//LProne" # provide a default value if unspecified

LowPotential = arcpy.GetParameterAsText(8)
if LowPotential == '#' or not LowPotential:
    LowPotential = ".//..//LPotential" # provide a default value if unspecified

HighPotential = arcpy.GetParameterAsText(9)
if HighPotential == '#' or not HighPotential:
    HighPotential = ".//..//HPotential" # provide a default value if unspecified

# Local variables:
Output_measurement = "DEGREE"
HighValue = HighProne
FloodPotential = LowPotential
Filter_value_high = "1"
Filter_value_low = "1"
ClassValue_times10 = "10"
Input_false_raster_or_constant_value = "0"
Input_false_raster_or_constant_value__2_ = "0"

# Process: Slope
arcpy.gp.Slope_sa(Dem40_FVG, SlopeDegree, Output_measurement, "1")

# Process: Slope Slection
arcpy.gp.RasterCalculator_sa("\"%SlopeDegree%\" <= 7", SlopeFlatClass)

# Process: Selection deposits
arcpy.Select_analysis(Harmonised_Geoloigcal_Map, Alluvium_Deposits, "[GE_EVENTENVIRONMENT] = 'basin_plane_setting' OR [DESCRIPTION__GEOLOGICUNIT_] LIKE '*alluvial*'")
# [GE_EVENTENVIRONMENT] represents the gdb attribute; in the system should be: for Geology1M gml:FeatureMember/ge:MappedFeature/ge:specification/ge:GeologicUnit/ge:geologicHistory/ge:GeologicEvent/ge:eventEnvironment attribute
# while for Geology100k gml:FeatureMember/gsml:MappedFeature/gsml:specification/gsmlgu:GeologicUnit/gsml:relatedFeature/gsmlga:geologicHistory/gsmlga:GeologicEvent/gsmlga:eventEnvironment/swe:Category identifier value 

# Process: Alluvium zone to Raster
arcpy.FeatureToRaster_conversion(Alluvium_Deposits, "GE_GEOLOGICUNITTYPE", AlluviumClass, "25")
# [GE_GEOLOGICUNITTYPE] represents the gdb attribute; in the system should be: for Geology1M gml:FeatureMember/ge:MappedFeature/ge:specification/ge:GeologicUnit/ge:geologicUnitType/ge:composition/ge:CompositionPart/ge:material attribute
# while for Geology100k gml:FeatureMember/gsml:MappedFeature/gsml:specification/gsmlgu:GeologicUnit/gsmlgu:geologicUnitType or.... attribute 

# Process: Select High Prone Area
arcpy.gp.Con_sa(SlopeFlatClass, AlluviumClass, HighProne, Input_false_raster_or_constant_value, "\"Value\" = 1")

# Process: MergeClassHigh
arcpy.gp.GreaterThanEqual_sa(HighProne, Filter_value_high, HighValue)

# Process: Times
arcpy.gp.Times_sa(HighValue, ClassValue_times10, HighPotential)

# Process: select Low Prone Area
arcpy.gp.Con_sa(SlopeFlatClass, AlluviumClass, LowProne, Input_false_raster_or_constant_value__2_, "\"Value\" = 0")

# Process: MergeClassLow
arcpy.gp.GreaterThanEqual_sa(LowProne, Filter_value_low, LowPotential)

# Process: Combine Potential Class
arcpy.gp.RasterCalculator_sa("\"%HighPotential%\" + \"%LowPotential%\"", FloodPotential)
