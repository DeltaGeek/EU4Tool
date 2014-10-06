package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.MapData;
import deltageek.eu4.model.Province;
import deltageek.eu4.util.MapUtilities;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ExportToModWorker extends SwingWorker<Void, Void> {
    private MapData mapData;
    private ProvincePane ui;
    private Path exportDirPath;

    public ExportToModWorker(MapData mapData, ProvincePane ui, Path exportDirPath){
        this.mapData = mapData;
        this.ui = ui;
        this.exportDirPath = exportDirPath;
    }

    @Override
    protected Void doInBackground() throws Exception {
        Path decisionPath = exportDirPath.resolve("decision.txt");
        Path modifiersPath = exportDirPath.resolve("modifiers.txt");
        Path localizationPath = exportDirPath.resolve("localization.yml");
        try (BufferedWriter decisionWriter = Files.newBufferedWriter(decisionPath,MapUtilities.ISO_CHARSET);
             BufferedWriter modifiersWriter = Files.newBufferedWriter(modifiersPath,MapUtilities.ISO_CHARSET);
             BufferedWriter localizationWriter = Files.newBufferedWriter(localizationPath,MapUtilities.UTF_CHARSET)){

            Set<Province> seenProvinces = new HashSet<>();
            writeDecisionHeader(decisionWriter);
            writeLocalizationHeader(localizationWriter);

            for(Province province : mapData.provinces){
                if(!province.riverCrossings.isEmpty())
//                    writeCrossingToData(decisionWriter, modifiersWriter, localizationWriter, seenProvinces, province);
                    writeCrossingFromData(decisionWriter, modifiersWriter, localizationWriter, province);
            }

            writeDecisionFooter(decisionWriter);
        }
        finally{
            return null;
        }
    }

    private void writeCrossingToData(BufferedWriter decisionWriter, BufferedWriter modifiersWriter, BufferedWriter localizationWriter, Set<Province> seenProvinces, Province province) throws IOException {
        decisionWriter.write("\n");
        decisionWriter.write(String.format("\t\t\t\t%d = {\n", province.id));

        for (Province destProvince : province.riverCrossings) {
            writeCrossingToDecisionEffect(decisionWriter, destProvince);
            if (!seenProvinces.contains(destProvince)) {
                seenProvinces.add(destProvince);
                writeCrossingToModifier(modifiersWriter, destProvince);
                writeCrossingToLocalization(localizationWriter, destProvince);
            }
        }

        decisionWriter.write("\t\t\t\t}\n");
    }

    private void writeCrossingToDecisionEffect(BufferedWriter writer, Province destProvince) throws IOException {
        writer.write("\t\t\t\t\tadd_province_modifier = {\n");
        writer.write(String.format("\t\t\t\t\t\tname = \"river_crossing_to_%s\"\n", destProvince.id));
        writer.write("\t\t\t\t\t}\n");
    }

    private void writeCrossingToModifier(BufferedWriter writer, Province destProvince) throws IOException {
        writer.write(String.format("river_crossing_to_%s {\n", destProvince.id));
        writer.write(" picture = \"river_crossing\"\n");
        writer.write("}\n");
    }

    private void writeCrossingToLocalization(BufferedWriter writer, Province destProvince) throws IOException {
        writer.write("\n");
        writer.write(String.format(" river_crossing_to_%s: \"River Crossing\"\n", destProvince.id));
        writer.write(String.format(" desc_river_crossing_to_%s: \"A river flows between this province and %s\"\n", destProvince.id, destProvince.name));
    }

    private void writeCrossingFromData(BufferedWriter decisionWriter, BufferedWriter modifiersWriter, BufferedWriter localizationWriter, Province province) throws IOException {
        writeCrossingFromDecisionEffect(decisionWriter, province);
        writeCrossingFromModifier(modifiersWriter, province);
        writeCrossingFromLocalization(localizationWriter, province);
    }

    private void writeCrossingFromDecisionEffect(BufferedWriter writer, Province sourceProvince) throws IOException {
        writer.write("\n");
        writer.write(String.format("\t\t\t\t%d = {\n", sourceProvince.id));
        writer.write("\t\t\t\t\tadd_province_modifier = {\n");
        writer.write(String.format("\t\t\t\t\t\tname = \"river_crossing_from_%s\"\n", sourceProvince.id));
        writer.write("\t\t\t\t\t\tduration = -1\n");
        writer.write("\t\t\t\t\t}\n");
        writer.write("\t\t\t\t}\n");
    }

    private void writeCrossingFromModifier(BufferedWriter writer, Province sourceProvince) throws IOException {
        writer.write(String.format("river_crossing_from_%s {\n", sourceProvince.id));
        writer.write(" picture = \"river_crossing\"\n");
        writer.write("}\n");
    }

    private void writeCrossingFromLocalization(BufferedWriter writer, Province sourceProvince) throws IOException {
        writer.write("\n");
        writer.write(String.format(" river_crossing_from_%s: \"River Crossing\"\n", sourceProvince.id));
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append(String.format(" desc_river_crossing_from_%s: |\n  A river flows between this province and the following neighbors\n", sourceProvince.id));
        for(Province destProvince : sourceProvince.riverCrossings){
            descriptionBuilder.append(String.format("   • %s\n", destProvince.name));
        }
        writer.write(descriptionBuilder.toString());
    }

    private void writeLocalizationHeader(BufferedWriter writer) throws IOException {
        writer.write("l_english:\n");
        writer.write(" add_river_crossings_title: \"Add river crossings to provinces\"\n");
        writer.write(" add_river_crossings_desc: \"Display possible river crossings in provinces\"\n");
    }

    private void writeDecisionHeader(BufferedWriter writer) throws IOException {
        writer.write("country_decisions = {\n");
        writer.write("\tadd_river_crossings = {\n");
        writer.write("\t\tmajor = yes\n\n");
        writer.write("\t\tpotential = {\n");
        writer.write("\t\t\tNOT = { has_global_flag = river_crossings_added }\n");
        writer.write("\t\t}\n\n");
        writer.write("\t\teffect = {\n");
        writer.write("\t\t\thidden_effect = {\n");
        writer.write("\t\t\t\tset_global_flag = river_crossings_added\n");
    }

    private void writeDecisionFooter(BufferedWriter decisionWriter) throws IOException {
        decisionWriter.write("\t\t\t}\n");
        decisionWriter.write("\t\t}\n");
        decisionWriter.write("\t}\n");
        decisionWriter.write("}");
    }

    @Override
    protected void done(){
        ui.setExportButtonEnabled(true);
    }

}
