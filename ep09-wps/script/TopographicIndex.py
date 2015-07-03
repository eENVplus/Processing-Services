# -*- coding: utf-8 -*-
# ---------------------------------------------------------------------------
# TopographicIndex.py
# Created on: 2014-02-28 by Carlo Cipolloni, Maria Pia Congi and Marco Pantaloni  - Geological Survey of Italy ISPRA
#   (generated by ArcGIS/ModelBuilder)
# Usage: TopographicIndex <Dem40_FVG> <SmothDem> <Acc> <FlowDir> <TanBeta> <CTI> <Cell_Size> 
# Description: That procedure should be performed using the slope layer elaborated in the previous procedure; 
#				allow to refine the detail of Flood zone and the aim is to elaborate a prone area based on the flow accumulation situation deduced from morphometric parameters of a Hydrographic basin, we have selected the Wetness Topographic Index (Bevan and Kirkby, 1979; Manfreda et al., 2011), because is independent from the available input data (i.e. Hierarchy river order).
#				At the end of the procedure the users should be able to define classification of final map based on the value frequency distribution.
# ---------------------------------------------------------------------------

# Import arcpy module
import arcpy

# Check out any necessary licenses
arcpy.CheckOutExtension("spatial")

# Script arguments
Dem40_FVG = arcpy.GetParameterAsText(0)
if Dem40_FVG == '#' or not Dem40_FVG:
    Dem40_FVG = "Dem40_FVG" # provide a default value if unspecified

SmothDem = arcpy.GetParameterAsText(1)

Acc = arcpy.GetParameterAsText(2)

FlowDir = arcpy.GetParameterAsText(3)

TanBeta = arcpy.GetParameterAsText(4)

CTI = arcpy.GetParameterAsText(5)

Cell_Size = arcpy.GetParameterAsText(6)
if Cell_Size == '#' or not Cell_Size:
    Cell_Size = "40" # provide a default value if unspecified

# Local variables:
Output_drop_raster = SmothDem
Slope40 = "./../Slope40"
RSlope = ".//..//RSlope"

# Process: FillDemError
arcpy.gp.Fill_sa(Dem40_FVG, SmothDem, "")

# Process: Flow Direction
arcpy.gp.FlowDirection_sa(SmothDem, FlowDir, "NORMAL", Output_drop_raster)

# Process: Flow Accumulation
arcpy.gp.FlowAccumulation_sa(FlowDir, Acc, "", "FLOAT")

# Process: RadiantConversion
arcpy.gp.RasterCalculator_sa("\"%Slope40%\" * 1.570796 / 90", RSlope)

# Process: RSlopeCorrection
arcpy.gp.RasterCalculator_sa("Con(\"%RSlope%\" > 0,Tan(\"%RSlope%\"),0.001)", TanBeta)

# Process: TopographicIndex
arcpy.gp.RasterCalculator_sa("Ln(((\"%Acc%\" + 1) * 40) / \"%TanBeta%\")", CTI)

# A re-classify procedure based on the frequancy value distribution should be implemented.