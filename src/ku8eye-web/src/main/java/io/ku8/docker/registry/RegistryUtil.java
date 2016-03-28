package io.ku8.docker.registry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;

public class RegistryUtil {
	public static void genImageManifestFile(File unzipedImagePath, String imageName, String tag) throws Exception {
		ImageManifest manifest = new ImageManifest();
		manifest.setName(imageName);
		manifest.setTag(tag);
		Collection<LayerPair> fslayers = new LinkedList<LayerPair>();

		File[] layerFiles = unzipedImagePath.listFiles();
		for (File theFile : layerFiles) {
			if (theFile.getName().equals("manifest.json") || theFile.getName().equals("repositories")) {
				continue;
			}
			File layFile = new File(theFile, "layer.tar");
			File jsonFile = new File(theFile, "json");
			V1LayerJson layerJson = new V1LayerJson();
			layerJson.setV1Compatibility(new String(Util.readFile(jsonFile), "utf-8"));

			String sharDigit = SHA256Digit.hash(layFile);
			FsLayer curLayer = new FsLayer();
			curLayer.setBlobSum("sha256:" + sharDigit);
			LayerPair layerPari = new LayerPair(layerJson, curLayer);
			fslayers.add(layerPari);
		}
		fslayers = reOrderLayers(fslayers);
		manifest.setFsLayers(toFsLayers(fslayers));
		manifest.setHistory(toJsonLayers(fslayers));
		// , Util.generateECKeyPair()
		String signMenifest = Util
				.signMenifest(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(manifest));
		ByteArrayInputStream menifestStream = new ByteArrayInputStream(signMenifest.getBytes("utf-8"));
		File meinfestFile = new File(unzipedImagePath, "manifest.json");
		Util.writeFile(menifestStream, meinfestFile);

	}

	private static Collection<LayerPair> reOrderLayers(Collection<LayerPair> allLayers) throws Exception {
		LinkedHashMap<String, LayerPair> resultLayers = new LinkedHashMap<String, LayerPair>(allLayers.size());
		ObjectMapper objMapper = new ObjectMapper();
		
		while (!allLayers.isEmpty()) {
			Iterator<LayerPair> itor = allLayers.iterator();
			while (itor.hasNext()) {
				LayerPair theLayer = itor.next();
				String v1Json = theLayer.layerJson.getV1Compatibility();
				V1JsonObj v1Obj = objMapper.readValue(v1Json, V1JsonObj.class);
				String layerId = null;
				if (v1Obj.getParent() == null) {
					layerId = v1Obj.getId();
				} else {
					if (resultLayers.containsKey(v1Obj.getParent())) {
						layerId = v1Obj.getId();
					}
				}
				if (layerId != null) {
					resultLayers.put(layerId, theLayer);
					itor.remove();
				}

			}
		}
		Collection<LayerPair> result=resultLayers.values();
		return result;
		
	}

	private static FsLayer[] toFsLayers(Collection<LayerPair> layerPairs) {
		FsLayer[] allLayers = new FsLayer[layerPairs.size()];
		int ind = allLayers.length-1;
		for (LayerPair pair : layerPairs) {
			allLayers[ind--] = pair.fsLayer;
		}
		return allLayers;
		// fsLayers.toArray(new FsLayer[fsLayers.size()])
	}

	private static V1LayerJson[] toJsonLayers(Collection<LayerPair> fslayers) {
		V1LayerJson[] allLayers = new V1LayerJson[fslayers.size()];
		int ind = allLayers.length-1;
		for (LayerPair pair : fslayers) {
			allLayers[ind--] = pair.layerJson;
		}
		return allLayers;
	}
}

class LayerPair {
	public V1LayerJson layerJson;
	public FsLayer fsLayer;

	public LayerPair(V1LayerJson layerJson, FsLayer fsLayer) {
		super();
		this.layerJson = layerJson;
		this.fsLayer = fsLayer;
	}

}
