package codegen.codetemplates;

import java.util.ArrayList;
import java.util.List;

/**
 * Template for allowing many different replacements for a single template. WIll
 * apply the template to each replacement set and create a repeated code
 * pattern.
 * 
 * This template does not allow for compound templates.
 * 
 * @author Justin Nelson
 * 
 */
public class CompoundCodeTemplate implements TemplateEntry {
	private String compoundPatternStartS = "<@@([^@]*)@@>";
	private String compoundPatternEndS = "<@@@@>";
	private String simplePatternS = "<@([^@]*)@>";
	/**
	 * Holds the possible replacements for the pattern
	 */
	private CodeTemplate templatePattern;
	/**
	 * Since this compound template will be repeated with many different
	 * replacements, we need a list of replacement mappings
	 */
	private List<CodeTemplate> templateReplacements;

	/**
	 * Creates a new compound template from a compound template definition.
	 * 
	 * @param template
	 */
	public CompoundCodeTemplate(String template) {
		templateReplacements = new ArrayList<CodeTemplate>();
		// the next two lines strip the compound pattern start and end from the
		// code if they exist.
		// this completely breaks if people try to nest compound templates. We
		// don't support that.
		template = template.replaceAll(compoundPatternStartS, "");
		template = template.replaceAll(compoundPatternEndS, "");
		templatePattern = new CodeTemplate(template);
	}

	/**
	 * Adds another mapping set to this compound pattern.
	 */
	public void addMappingSet(CodeTemplate mappingSet) {
		templateReplacements.add(mappingSet);
	}

	/**
	 * @return a copy of the template pattern that is empty. Used to give a user
	 *         an empty template that then can then fill and add to the list of
	 *         filled templates.
	 */
	public CodeTemplate getEmptyTemplate() {
		return templatePattern.copyEmptyTemplate();
	}

	@Override
	public String codeReplacement() {
		StringBuilder bldr = new StringBuilder();
		for(CodeTemplate template: templateReplacements){
			bldr.append(template.codeReplacement());
			bldr.append("\n");
		}
		return bldr.toString();
	}
}
